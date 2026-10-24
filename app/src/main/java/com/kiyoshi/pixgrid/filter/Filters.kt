package com.kiyoshi.pixgrid.filter

import androidx.compose.ui.graphics.ColorMatrix

/**
 * Filter berbasis ColorMatrix — jalan di GPU lewat Compose, tanpa RenderScript.
 * Setiap filter punya nilai default 0f..1f untuk [strength] sehingga bisa di-blend
 * dengan matriks identitas.
 */
data class PhotoFilter(
    val id: String,
    val label: String,
    val matrix: ColorMatrix,
)

object Filters {

    private val identity = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )

    private fun mat(vararg v: Float) = ColorMatrix(v.copyOf(20))

    val all: List<PhotoFilter> = listOf(
        PhotoFilter("none", "Asli", mat(*identity)),

        // Saturasi
        PhotoFilter(
            "vivid", "Vivid",
            mat(
                1.35f, -0.15f, -0.10f, 0f, 0f,
                -0.10f, 1.35f, -0.15f, 0f, 0f,
                -0.10f, -0.15f, 1.35f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        PhotoFilter(
            "muted", "Muted",
            mat(
                0.82f, 0.09f, 0.09f, 0f, 0f,
                0.09f, 0.82f, 0.09f, 0f, 0f,
                0.09f, 0.09f, 0.82f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        PhotoFilter(
            "mono", "Mono",
            mat(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Hangat / dingin
        PhotoFilter(
            "warm", "Hangat",
            mat(
                1.12f, 0f, 0f, 0f, 14f,
                0f, 1.02f, 0f, 0f, 4f,
                0f, 0f, 0.88f, 0f, -6f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        PhotoFilter(
            "cool", "Dingin",
            mat(
                0.88f, 0f, 0f, 0f, -6f,
                0f, 1.00f, 0f, 0f, 2f,
                0f, 0f, 1.14f, 0f, 16f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Kontras & fade
        PhotoFilter(
            "fade", "Fade",
            mat(
                0.78f, 0f, 0f, 0f, 30f,
                0f, 0.78f, 0f, 0f, 30f,
                0f, 0f, 0.78f, 0f, 30f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        PhotoFilter(
            "punch", "Punch",
            mat(
                1.28f, 0f, 0f, 0f, -34f,
                0f, 1.28f, 0f, 0f, -34f,
                0f, 0f, 1.28f, 0f, -34f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Sepia & vintage
        PhotoFilter(
            "sepia", "Sepia",
            mat(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        PhotoFilter(
            "vintage", "Vintage",
            mat(
                0.90f, 0.20f, 0.10f, 0f, 8f,
                0.10f, 0.85f, 0.10f, 0f, 4f,
                0.05f, 0.15f, 0.75f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Noir
        PhotoFilter(
            "noir", "Noir",
            mat(
                0.28f, 0.55f, 0.17f, 0f, -8f,
                0.28f, 0.55f, 0.17f, 0f, -8f,
                0.28f, 0.55f, 0.17f, 0f, -8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Film look
        PhotoFilter(
            "film", "Film",
            mat(
                1.05f, 0.05f, 0f, 0f, 6f,
                0.02f, 1.02f, 0.02f, 0f, 2f,
                0f, 0.05f, 0.98f, 0f, 8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // Cyan/teal cinematic
        PhotoFilter(
            "teal", "Teal",
            mat(
                0.92f, 0f, 0.10f, 0f, -4f,
                0f, 1.04f, 0.06f, 0f, 2f,
                0.06f, 0f, 1.10f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
    )

    fun byId(id: String): PhotoFilter = all.firstOrNull { it.id == id } ?: all[0]

    /** Campur matriks filter dengan identitas sesuai kekuatan 0f..1f. */
    fun blended(filterId: String, strength: Float): ColorMatrix {
        val f = byId(filterId)
        if (f.id == "none") return ColorMatrix()
        val s = strength.coerceIn(0f, 1f)
        val src = f.matrix.values
        val out = FloatArray(20)
        for (i in 0 until 20) {
            out[i] = identity[i] * (1f - s) + src[i] * s
        }
        return ColorMatrix(out)
    }

    /** Preset kecerahan/kontras/saturasi manual. */
    fun manual(brightness: Float, contrast: Float, saturation: Float): ColorMatrix {
        val b = brightness * 255f
        val c = contrast
        val sat = saturation
        val lr = 0.2126f
        val lg = 0.7152f
        val lb = 0.0722f
        val sr = (1 - sat) * lr
        val sg = (1 - sat) * lg
        val sb = (1 - sat) * lb
        val m = floatArrayOf(
            c * (sr + sat), c * sg, c * sb, 0f, b,
            c * sr, c * (sg + sat), c * sb, 0f, b,
            c * sr, c * sg, c * (sb + sat), 0f, b,
            0f, 0f, 0f, 1f, 0f
        )
        return ColorMatrix(m)
    }
}
