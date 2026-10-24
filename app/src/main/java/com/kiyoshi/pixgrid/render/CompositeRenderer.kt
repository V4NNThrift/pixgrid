package com.kiyoshi.pixgrid.render

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
        val key = "$uri@${reqWidth}x$reqHeight"
        bitmapCache[key]?.let { if (!it.isRecycled) return it }

        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            var sample = 1
            while (bounds.outWidth / (sample * 2) >= reqWidth &&
                bounds.outHeight / (sample * 2) >= reqHeight
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

    /** Decode gambar dari assets dengan sampling. */
    private fun decodeAsset(
        context: Context,
        assetPath: String,
        reqWidth: Int,
    ): Bitmap? {
        val key = "asset:$assetPath@$reqWidth"
        bitmapCache[key]?.let { if (!it.isRecycled) return it }
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0) return null

            var sample = 1
            while (bounds.outWidth / (sample * 2) >= reqWidth) sample *= 2

            val opts = BitmapFactory.Options().apply {
                inSampleSize = max(1, sample)
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val bmp = context.assets.open(assetPath).use {
                BitmapFactory.decodeStream(it, null, opts)
            } ?: return null

            if (bitmapCache.size >= 12) bitmapCache.clear()
            bitmapCache[key] = bmp
            bmp
        } catch (t: Throwable) {
            null
        }
    }

    /** Muat Typeface dari res/font, fallback ke default. */
    private fun loadTypeface(context: Context, resName: String): android.graphics.Typeface {
        if (resName.isBlank()) return android.graphics.Typeface.DEFAULT
        val key = "tf:$resName"
        typefaceCache[key]?.let { return it }
        return try {
            val resId = context.resources.getIdentifier(resName, "font", context.packageName)
            val tf = if (resId != 0) {
                androidx.core.content.res.ResourcesCompat.getFont(context, resId)
                    ?: android.graphics.Typeface.DEFAULT
            } else {
                android.graphics.Typeface.DEFAULT
            }
            typefaceCache[key] = tf
            tf
        } catch (t: Throwable) {
            android.graphics.Typeface.DEFAULT
        }
    }

    private val typefaceCache = mutableMapOf<String, android.graphics.Typeface>()

    private fun applyExif(context: Context, uri: String, bmp: Bitmap): Bitmap {
        return try {
            val orientation = context.contentResolver
                .openInputStream(android.net.Uri.parse(uri))
                ?.use {
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
                    m.postRotate(90f)
                    m.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    m.postRotate(270f)
                    m.postScale(-1f, 1f)
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
     */
    fun render(
        context: Context,
        layout: GridLayout,
        slots: List<PhotoSlot?>,
        canvasStyle: CanvasStyle,
        texts: List<TextItem>,
        stickers: List<StickerItem>,
        doodles: List<Doodle>,
        targetWidth: Int = 1080,
    ): Bitmap {
        val w = targetWidth.coerceIn(240, EXPORT_MAX)
        val h = (w / canvasStyle.aspectRatio).toInt().coerceIn(240, EXPORT_MAX)

        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val fw = w.toFloat()
        val fh = h.toFloat()

        // Latar belakang: asset gambar kalau ada, kalau tidak warna solid
        val bgAsset = canvasStyle.backgroundAsset
        val bgBitmap = if (!bgAsset.isNullOrBlank()) decodeAsset(context, bgAsset, w) else null

        if (bgBitmap != null) {
            // center-crop agar memenuhi kanvas
            val bw = bgBitmap.width.toFloat()
            val bh = bgBitmap.height.toFloat()
            val scale = max(fw / bw, fh / bh)
            val dw = bw * scale
            val dh = bh * scale
            val left = (fw - dw) / 2f
            val top = (fh - dh) / 2f
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(
                bgBitmap, null,
                RectF(left, top, left + dw, top + dh),
                bgPaint
            )
        } else {
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            bgPaint.color = canvasStyle.background.toArgbInt()
            canvas.drawRect(0f, 0f, fw, fh, bgPaint)
        }

        // Blur latar dari foto pertama
        if (canvasStyle.backgroundBlur) {
            val first = slots.firstOrNull { it != null }
            if (first != null) {
                val bmp = decodeSampled(context, first.uri, w / 4, h / 4)
                if (bmp != null) {
                    val tinyW = max(1, w / 18)
                    val tinyH = max(1, h / 18)
                    val tiny = Bitmap.createScaledBitmap(bmp, tinyW, tinyH, true)
                    val blurPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
                    blurPaint.alpha = 160
                    canvas.drawBitmap(tiny, null, RectF(0f, 0f, fw, fh), blurPaint)
                    if (tiny !== bmp) tiny.recycle()
                }
            }
        }

        val shortest = min(fw, fh)
        val outer = canvasStyle.border.outerPadding * shortest
        val thickness = canvasStyle.border.thickness * shortest
        val radius = canvasStyle.border.cornerRadius * shortest

        val contentW = fw - outer * 2
        val contentH = fh - outer * 2

        // Gambar tiap sel
        layout.cells.forEachIndexed { index, cell ->
            val slot = slots.getOrNull(index) ?: return@forEachIndexed

            val cellLeft = outer + cell.x * contentW
            val cellTop = outer + cell.y * contentH
            val cellW = cell.w * contentW
            val cellH = cell.h * contentH

            val inset = thickness / 2f
            val ix = cellLeft + inset
            val iy = cellTop + inset
            val iw = cellW - thickness
            val ih = cellH - thickness
            if (iw <= 1f || ih <= 1f) return@forEachIndexed

            val rect = RectF(ix, iy, ix + iw, iy + ih)

            canvas.save()
            val clipPath = Path()
            clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
            canvas.clipPath(clipPath)

            val bmp = decodeSampled(
                context, slot.uri,
                iw.toInt().coerceAtLeast(1),
                ih.toInt().coerceAtLeast(1)
            )
            if (bmp != null) {
                drawSlotBitmap(canvas, bmp, slot, rect)
            } else {
                val ph = Paint(Paint.ANTI_ALIAS_FLAG)
                ph.color = 0xFF1A1A22.toInt()
                canvas.drawRect(rect, ph)
            }
            canvas.restore()
        }

        // Border overlay
        if (thickness > 0f) {
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            borderPaint.color = canvasStyle.border.color.toArgbInt()
            borderPaint.setStyle(Paint.Style.STROKE)
            borderPaint.strokeWidth = thickness

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
                canvas.drawRoundRect(r, radius, radius, borderPaint)
            }
        }

        // Teks
        texts.forEach { t ->
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.color = t.color.toArgbInt()
            textPaint.textSize = t.size * shortest
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.isFakeBoldText = t.bold
            textPaint.textSkewX = if (t.italic) -0.25f else 0f
            if (t.fontRes.isNotBlank()) {
                textPaint.typeface = loadTypeface(context, t.fontRes)
            }
            if (t.shadow) {
                textPaint.setShadowLayer(
                    textPaint.textSize * 0.12f, 0f, textPaint.textSize * 0.05f,
                    0xAA000000.toInt()
                )
            }
            canvas.save()
            canvas.rotate(t.rotation, t.x * fw, t.y * fh)
            canvas.drawText(t.text, t.x * fw, t.y * fh, textPaint)
            canvas.restore()
        }

        // Sticker
        stickers.forEach { st ->
            val cx = st.x * fw
            val cy = st.y * fh
            val sizePx = st.size * shortest

            canvas.save()
            canvas.rotate(st.rotation, cx, cy)

            if (st.asset.isNotBlank()) {
                val bmp = decodeAsset(context, st.asset, sizePx.toInt().coerceAtLeast(8))
                if (bmp != null) {
                    val half = sizePx / 2f
                    canvas.drawBitmap(
                        bmp, null,
                        RectF(cx - half, cy - half, cx + half, cy + half),
                        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                    )
                }
            } else if (st.glyph.isNotBlank()) {
                val stickerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                stickerPaint.textSize = sizePx
                stickerPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(st.glyph, cx, cy, stickerPaint)
            }
            canvas.restore()
        }

        // Doodle
        doodles.forEach { d ->
            if (d.points.size < 2) return@forEach
            val doodlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            doodlePaint.setStyle(Paint.Style.STROKE)
            doodlePaint.strokeCap = Paint.Cap.ROUND
            doodlePaint.strokeJoin = Paint.Join.ROUND

            if (d.eraser) {
                doodlePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                doodlePaint.color = android.graphics.Color.TRANSPARENT
                doodlePaint.strokeWidth = d.width * shortest * 2.5f
            } else {
                doodlePaint.color = d.color.toArgbInt()
                doodlePaint.strokeWidth = d.width * shortest
            }

            val path = Path()
            d.points.forEachIndexed { i, p ->
                val px = p.first * fw
                val py = p.second * fh
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            canvas.drawPath(path, doodlePaint)
        }

        return out
    }

    private fun drawSlotBitmap(
        canvas: Canvas,
        bmp: Bitmap,
        slot: PhotoSlot,
        rect: RectF,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (slot.filterId != "none") {
            val cmArr = Filters.blendedFloatArray(slot.filterId, slot.filterStrength)
            paint.colorFilter = ColorMatrixColorFilter(cmArr)
        }

        val srcX = slot.cropX * bmp.width
        val srcY = slot.cropY * bmp.height
        val srcW = (slot.cropWidth * bmp.width).coerceAtLeast(1f)
        val srcH = (slot.cropHeight * bmp.height).coerceAtLeast(1f)
        val src = android.graphics.Rect(
            srcX.coerceIn(0f, bmp.width - 1f).toInt(),
            srcY.coerceIn(0f, bmp.height - 1f).toInt(),
            (srcX + srcW).coerceIn(1f, bmp.width.toFloat()).toInt(),
            (srcY + srcH).coerceIn(1f, bmp.height.toFloat()).toInt(),
        )

        canvas.save()

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

        if (slot.scale != 1f) {
            val w2 = drawRect.width() * slot.scale
            val h2 = drawRect.height() * slot.scale
            drawRect.set(
                cx - w2 / 2f,
                cy - h2 / 2f,
                cx + w2 / 2f,
                cy + h2 / 2f,
            )
        }

        canvas.drawBitmap(bmp, src, drawRect, paint)
        canvas.restore()
    }

    /** Konversi Compose Color ke ARGB Int tanpa bergantung pada composable. */
    private fun androidx.compose.ui.graphics.Color.toArgbInt(): Int {
        val a = (alpha * 255f).toInt().coerceIn(0, 255)
        val r = (red * 255f).toInt().coerceIn(0, 255)
        val g = (green * 255f).toInt().coerceIn(0, 255)
        val b = (blue * 255f).toInt().coerceIn(0, 255)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    /** Simpan ke galeri. */
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
                resolver.openOutputStream(uri)?.use { outs ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outs)
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
                FileOutputStream(file).use { outs ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outs)
                }
                android.net.Uri.fromFile(file)
            }
        } catch (t: Throwable) {
            null
        }
    }

    /** Simpan ke cache untuk dibagikan. */
    fun saveToCache(context: Context, bitmap: Bitmap): File? {
        return try {
            val dir = File(context.cacheDir, "share")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "pixgrid_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { outs ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outs)
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
