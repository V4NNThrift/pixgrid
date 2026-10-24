package com.kiyoshi.pixgrid.render

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.kiyoshi.pixgrid.filter.Filters
import com.kiyoshi.pixgrid.model.CanvasStyle
import com.kiyoshi.pixgrid.model.Doodle
import com.kiyoshi.pixgrid.model.GridLayout
import com.kiyoshi.pixgrid.model.PhotoSlot
import com.kiyoshi.pixgrid.model.StickerItem
import com.kiyoshi.pixgrid.model.TextItem
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/**
 * Merender komposisi penuh menjadi Bitmap resolusi tinggi.
 * Semua koordinat model berupa fraksi 0f..1f, dikali ukuran output.
 */
object CompositeRenderer {

    const val EXPORT_MAX = 3000

    private val bitmapCache = object : LinkedHashMap<String, Bitmap>(8, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>?): Boolean =
            size > 12
    }

    private fun decodeSampled(
        context: Context,
        uri: String,
        reqWidth: Int,
        reqHeight: Int,
    ): Bitmap? {
        val key = "$uri@$reqWidth x$reqHeight"
        bitmapCache[key]?.let { if (!it.isRecycled) return it }

        return try {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use {
                BitmapFactory.decodeStream(it, null, opts)
            }
            if (opts.outWidth <= 0 || opts.outHeight <= 0) return null

            var sample = 1
            while (opts.outWidth / (sample * 2) >= reqWidth &&
                opts.outHeight / (sample * 2) >= reqHeight
            ) {
                sample *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply {
                inSampleSize = max(1, sample)
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val bmp = context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use {
                BitmapFactory.decodeStream(it, null, decodeOpts)
            } ?: return null

            val oriented = applyExif(context, uri, bmp)
            if (bitmapCache.size >= 12) bitmapCache.clear()
            bitmapCache[key] = oriented
            oriented
        } catch (t: Throwable) {
            null
        }
    }

    private fun applyExif(context: Context, uri: String, bmp: Bitmap): Bitmap {
        return try {
            val stream: InputStream? = context.contentResolver.openInputStream(android.net.Uri.parse(uri))
            val orientation = stream?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            val m = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    m.postRotate(90f); m.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    m.postRotate(270f); m.postScale(-1f, 1f)
                }
            }
            if (m.isIdentity) bmp
            else Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
                .also { if (it !== bmp) bmp.recycle() }
        } catch (t: Throwable) {
            bmp
        }
    }

    /**
     * Render komposisi ke Bitmap.
     * @param targetWidth lebar output; tinggi dihitung dari aspect ratio style.
     */
    fun render(
        context: Context,
        layout: GridLayout,
        slots: List<PhotoSlot?>,
        style: CanvasStyle,
        texts: List<TextItem>,
        stickers: List<StickerItem>,
        doodles: List<Doodle>,
        targetWidth: Int = 1080,
    ): Bitmap {
        val w = targetWidth.coerceIn(240, EXPORT_MAX)
        val h = (w / style.aspectRatio).toInt().coerceIn(240, EXPORT_MAX)

        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val fw = w.toFloat()
        val fh = h.toFloat()

        // Latar belakang
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = style.background }
        canvas.drawRect(0f, 0f, fw, fh, bgPaint)

        // Blur latar dari foto pertama (opsional)
        if (style.backgroundBlur) {
            val first = slots.firstOrNull { it != null }
            if (first != null) {
                val bmp = decodeSampled(context, first.uri, w / 4, h / 4)
                if (bmp != null) {
                    val tiny = Bitmap.createScaledBitmap(
                        bmp, max(1, w / 16), max(1, h / 16), true
                    )
                    val blurred = tiny
                    val dst = RectF(0f, 0f, fw, fh)
                    canvas.drawBitmap(blurred, null, dst, Paint().apply { alpha = 150 })
                    if (blurred !== bmp) blurred.recycle()
                }
            }
        }

        val shortest = min(fw, fh)
        val outer = style.border.outerPadding * shortest
        val thickness = style.border.thickness * shortest
        val radius = style.border.cornerRadius * shortest

        val contentW = fw - outer * 2
        val contentH = fh - outer * 2

        // Gambar tiap sel
        layout.cells.forEachIndexed { index, cell ->
            val slot = slots.getOrNull(index) ?: return@forEachIndexed

            val cellLeft = outer + cell.x * contentW
            val cellTop = outer + cell.y * contentH
            val cellW = cell.w * contentW
            val cellH = cell.h * contentH

            // inset border
            val inset = thickness / 2f
            val ix = cellLeft + inset
            val iy = cellTop + inset
            val iw = cellW - thickness
            val ih = cellH - thickness
            if (iw <= 1f || ih <= 1f) return@forEachIndexed

            val rect = RectF(ix, iy, ix + iw, iy + ih)

            canvas.save()
            if (radius > 0f) {
                val path = android.graphics.Path().apply {
                    addRoundRect(rect, radius, radius, android.graphics.Path.Direction.CW)
                }
                canvas.clipPath(path)
            } else {
                canvas.clipRect(rect)
            }

            val bmp = decodeSampled(context, slot.uri, iw.toInt().coerceAtLeast(1), ih.toInt().coerceAtLeast(1))
            if (bmp != null) {
                drawSlotBitmap(canvas, bmp, slot, rect)
            } else {
                canvas.drawRect(rect, Paint().apply { color = 0xFF1A1A22.toInt() })
            }
            canvas.restore()
        }

        // Border overlay (garis di antara sel)
        if (thickness > 0f) {
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.border.color
                style = Paint.Style.STROKE
                strokeWidth = thickness
            }
            layout.cells.forEach { cell ->
                val cellLeft = outer + cell.x * contentW
                val cellTop = outer + cell.y * contentH
                val cellW = cell.w * contentW
                val cellH = cell.h * contentH
                val r = RectF(
                    cellLeft + thickness / 2f,
                    cellTop + thickness / 2f,
                    cellLeft + cellW - thickness / 2f,
                    cellTop + cellH - thickness / 2f
                )
                if (radius > 0f) {
                    canvas.drawRoundRect(r, radius, radius, borderPaint)
                } else {
                    canvas.drawRect(r, borderPaint)
                }
            }
        }

        // Teks
        texts.forEach { t ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = t.color
                textSize = t.size * shortest
                textAlign = Paint.Align.CENTER
                isFakeBoldText = t.bold
                textSkewX = if (t.italic) -0.25f else 0f
                if (t.shadow) setShadowLayer(textSize * 0.12f, 0f, textSize * 0.05f, 0xAA000000.toInt())
            }
            canvas.save()
            canvas.rotate(t.rotation, t.x * fw, t.y * fh)
            canvas.drawText(t.text, t.x * fw, t.y * fh, paint)
            canvas.restore()
        }

        // Sticker
        stickers.forEach { s ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = s.size * shortest
                textAlign = Paint.Align.CENTER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    typeface = android.graphics.Typeface.DEFAULT
                }
            }
            canvas.save()
            canvas.rotate(s.rotation, s.x * fw, s.y * fh)
            canvas.drawText(s.glyph, s.x * fw, s.y * fh, paint)
            canvas.restore()
        }

        // Doodle
        doodles.forEach { d ->
            if (d.points.size < 2) return@forEach
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                strokeWidth = d.width * shortest
                color = d.color
            }
            if (d.eraser) {
                paint.xfermode = android.graphics.PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.CLEAR
                )
                paint.color = android.graphics.Color.TRANSPARENT
                paint.strokeWidth = d.width * shortest * 2.5f
                paint.style = Paint.Style.STROKE
            }
            val path = android.graphics.Path()
            d.points.forEachIndexed { i, p ->
                val px = p.first * fw
                val py = p.second * fh
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            canvas.drawPath(path, paint)
        }

        return out
    }

    private fun drawSlotBitmap(
        canvas: Canvas,
        bmp: Bitmap,
        slot: PhotoSlot,
        rect: RectF,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            if (slot.filterId != "none") {
                val cm = Filters.blended(slot.filterId, slot.filterStrength)
                colorFilter = ColorMatrixColorFilter(cm)
            }
        }

        // Crop area sumber
        val srcX = slot.cropX * bmp.width
        val srcY = slot.cropY * bmp.height
        val srcW = (slot.cropWidth * bmp.width).coerceAtLeast(1f)
        val srcH = (slot.cropHeight * bmp.height).coerceAtLeast(1f)
        val src = RectF(
            srcX.coerceIn(0f, bmp.width - 1f),
            srcY.coerceIn(0f, bmp.height - 1f),
            (srcX + srcW).coerceIn(1f, bmp.width.toFloat()),
            (srcY + srcH).coerceIn(1f, bmp.height.toFloat()),
        )

        canvas.save()

        // Terapkan rotasi + flip + skala + offset di sekitar pusat sel
        val cx = rect.centerX()
        val cy = rect.centerY()
        canvas.rotate(slot.rotation, cx, cy)

        val scaleX = if (slot.flipH) -1f else 1f
        val scaleY = if (slot.flipV) -1f else 1f
        if (scaleX != 1f || scaleY != 1f) {
            canvas.scale(scaleX, scaleY, cx, cy)
        }

        val drawRect = RectF(
            rect.left + slot.offsetX * rect.width(),
            rect.top + slot.offsetY * rect.height(),
            rect.right + slot.offsetX * rect.width(),
            rect.bottom + slot.offsetY * rect.height(),
        )

        // Skala zoom di sekitar pusat
        if (slot.scale != 1f) {
            val w2 = drawRect.width() * slot.scale
            val h2 = drawRect.height() * slot.scale
            drawRect.set(
                rect.centerX() - w2 / 2f,
                rect.centerY() - h2 / 2f,
                rect.centerX() + w2 / 2f,
                rect.centerY() + h2 / 2f,
            )
        }

        canvas.drawBitmap(bmp, src, drawRect, paint)
        canvas.restore()
    }

    /** Simpan bitmap ke galeri. Mengembalikan URI atau null jika gagal. */
    fun saveToGallery(context: Context, bitmap: Bitmap): android.net.Uri? {
        val name = "PixGrid_${System.currentTimeMillis()}.png"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PixGrid")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return null
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                } ?: return null
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "PixGrid"
                )
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, name)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                android.net.Uri.fromFile(file)
            }
        } catch (t: Throwable) {
            null
        }
    }

    /** Simpan bitmap ke cache internal sebagai file (untuk share). */
    fun saveToCache(context: Context, bitmap: Bitmap): File? {
        return try {
            val dir = File(context.cacheDir, "share")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "pixgrid_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        } catch (t: Throwable) {
            null
        }
    }

    fun clearCache() {
        bitmapCache.values.forEach { if (!it.isRecycled) it.recycle() }
        bitmapCache.clear()
    }
}
