package com.kiyoshi.pixgrid.model

import androidx.compose.ui.graphics.Color

/**
 * Satu foto yang ditempatkan di dalam sel grid.
 * [cropX]/[cropY]/[cropWidth]/[cropHeight] adalah area sumber yang dipakai (0f..1f).
 */
data class PhotoSlot(
    val uri: String,
    val cropX: Float = 0f,
    val cropY: Float = 0f,
    val cropWidth: Float = 1f,
    val cropHeight: Float = 1f,
    val rotation: Float = 0f,
    val flipH: Boolean = false,
    val flipV: Boolean = false,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val filterId: String = "none",
    val filterStrength: Float = 1f,
)

/** Daftar foto aktif, satu per sel. Null = sel kosong. */
typealias SlotList = List<PhotoSlot?>

data class BorderStyle(
    val thickness: Float = 0.018f,   // fraksi dari sisi terpendek kanvas
    val color: Color = Color.White,
    val cornerRadius: Float = 0f,   // fraksi sisi terpendek
    val outerPadding: Float = 0.02f,
)

data class CanvasStyle(
    val aspectRatio: Float = 1f,    // width / height
    val background: Color = Color.Black,
    val backgroundBlur: Boolean = false,
    val border: BorderStyle = BorderStyle(),
)

/** Teks bebas di atas kanvas. Posisi & ukuran dalam fraksi 0f..1f. */
data class TextItem(
    val id: String,
    val text: String,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val size: Float = 0.08f,
    val color: Color = Color.White,
    val rotation: Float = 0f,
    val fontAsset: String = "",
    val bold: Boolean = false,
    val italic: Boolean = false,
    val shadow: Boolean = true,
)

/** Sticker emoji/glyph. [glyph] adalah karakter yang digambar. */
data class StickerItem(
    val id: String,
    val glyph: String,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val size: Float = 0.18f,
    val rotation: Float = 0f,
)

/** Goresan bebas. Poin dalam fraksi kanvas. */
data class Doodle(
    val id: String,
    val points: List<Pair<Float, Float>>,
    val color: Color = Color(0xFFFF7A45),
    val width: Float = 0.008f,
    val eraser: Boolean = false,
)

enum class LayoutFamily(val label: String) {
    GRID("Grid"),
    MAGAZINE("Majalah"),
    POLAROID("Polaroid"),
    STRIP("Strip"),
    CUSTOM("Bebas"),
}

/**
 * Definisi layout: daftar sel dengan rect ternormalisasi (0f..1f).
 */
data class GridLayout(
    val id: String,
    val label: String,
    val family: LayoutFamily,
    val cells: List<CellRect>,
) {
    val count: Int get() = cells.size
}

data class CellRect(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
)

object Layouts {

    private fun grid(cols: Int, rows: Int): List<CellRect> {
        val list = mutableListOf<CellRect>()
        val cw = 1f / cols
        val ch = 1f / rows
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                list.add(CellRect(c * cw, r * ch, cw, ch))
            }
        }
        return list
    }

    val all: List<GridLayout> = listOf(
        GridLayout("grid2", "2 × 2", LayoutFamily.GRID, grid(2, 2)),
        GridLayout("grid3", "3 × 3", LayoutFamily.GRID, grid(3, 3)),
        GridLayout("grid4", "4 × 4", LayoutFamily.GRID, grid(4, 4)),
        GridLayout("g23", "2 × 3", LayoutFamily.GRID, grid(2, 3)),
        GridLayout("g32", "3 × 2", LayoutFamily.GRID, grid(3, 2)),
        GridLayout("g15", "1 × 5", LayoutFamily.GRID, grid(1, 5)),

        // Majalah: satu besar + sisanya kecil
        GridLayout(
            "mag1", "Majalah 1", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 1f, 0.62f),
                CellRect(0f, 0.62f, 0.5f, 0.38f),
                CellRect(0.5f, 0.62f, 0.5f, 0.38f),
            )
        ),
        GridLayout(
            "mag2", "Majalah 2", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 0.5f, 0.5f),
                CellRect(0.5f, 0f, 0.5f, 0.5f),
                CellRect(0f, 0.5f, 1f, 0.5f),
            )
        ),
        GridLayout(
            "mag3", "Majalah 3", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 0.62f, 1f),
                CellRect(0.62f, 0f, 0.38f, 0.5f),
                CellRect(0.62f, 0.5f, 0.38f, 0.5f),
            )
        ),
        GridLayout(
            "mag4", "Sorot", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 1f, 0.72f),
                CellRect(0f, 0.72f, 0.25f, 0.28f),
                CellRect(0.25f, 0.72f, 0.25f, 0.28f),
                CellRect(0.5f, 0.72f, 0.25f, 0.28f),
                CellRect(0.75f, 0.72f, 0.25f, 0.28f),
            )
        ),

        // Polaroid: bingkai ala foto instan
        GridLayout(
            "pol1", "Polaroid", LayoutFamily.POLAROID,
            listOf(CellRect(0.08f, 0.06f, 0.84f, 0.72f))
        ),
        GridLayout(
            "pol2", "Polaroid ×2", LayoutFamily.POLAROID,
            listOf(
                CellRect(0.05f, 0.08f, 0.42f, 0.62f),
                CellRect(0.53f, 0.08f, 0.42f, 0.62f),
            )
        ),

        // Strip: cocok untuk fotobooth
        GridLayout(
            "strip3", "Strip 3", LayoutFamily.STRIP,
            listOf(
                CellRect(0.14f, 0.03f, 0.72f, 0.29f),
                CellRect(0.14f, 0.35f, 0.72f, 0.29f),
                CellRect(0.14f, 0.67f, 0.72f, 0.29f),
            )
        ),
        GridLayout(
            "strip4", "Strip 4", LayoutFamily.STRIP,
            listOf(
                CellRect(0.16f, 0.02f, 0.68f, 0.22f),
                CellRect(0.16f, 0.26f, 0.68f, 0.22f),
                CellRect(0.16f, 0.50f, 0.68f, 0.22f),
                CellRect(0.16f, 0.74f, 0.68f, 0.22f),
            )
        ),

        // Asimetris
        GridLayout(
            "a1", "Asimetris", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 0.66f, 0.5f),
                CellRect(0.66f, 0f, 0.34f, 0.5f),
                CellRect(0f, 0.5f, 0.34f, 0.5f),
                CellRect(0.34f, 0.5f, 0.66f, 0.5f),
            )
        ),
        GridLayout(
            "a2", "Tangga", LayoutFamily.MAGAZINE,
            listOf(
                CellRect(0f, 0f, 0.5f, 0.34f),
                CellRect(0.5f, 0f, 0.5f, 0.34f),
                CellRect(0f, 0.34f, 0.5f, 0.33f),
                CellRect(0.5f, 0.34f, 0.5f, 0.33f),
                CellRect(0f, 0.67f, 0.5f, 0.33f),
                CellRect(0.5f, 0.67f, 0.5f, 0.33f),
            )
        ),
    )

    fun byId(id: String): GridLayout = all.firstOrNull { it.id == id } ?: all[0]
}

object AspectPresets {
    val all: List<Pair<String, Float>> = listOf(
        "1:1" to 1f,
        "4:5" to 4f / 5f,
        "3:4" to 3f / 4f,
        "2:3" to 2f / 3f,
        "9:16" to 9f / 16f,
        "16:9" to 16f / 9f,
        "4:3" to 4f / 3f,
    )
}
