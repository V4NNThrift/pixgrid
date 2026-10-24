package com.kiyoshi.pixgrid.model

import androidx.compose.ui.graphics.Color

/** Font yang dibundel di assets/fonts. */
data class FontChoice(
    val id: String,
    val label: String,
    /** Nama resource di res/font, kosong = font bawaan sistem. */
    val resName: String,
) {
    /** Path aset lama — dipertahankan untuk kompatibilitas renderer teks. */
    val asset: String get() = ""
}

val BundledFonts: List<FontChoice> = listOf(
    FontChoice("default", "Bawaan", ""),
    FontChoice("poppins", "Poppins", "poppins_bold"),
    FontChoice("poppins_reg", "Poppins R", "poppins_regular"),
    FontChoice("bebas", "Bebas", "bebasneue_regular"),
    FontChoice("anton", "Anton", "anton_regular"),
    FontChoice("oswald", "Oswald", "oswald_bold"),
    FontChoice("montserrat", "Montserrat", "montserrat_bold"),
    FontChoice("playfair", "Playfair", "playfairdisplay_bold"),
    FontChoice("lobster", "Lobster", "lobster_regular"),
    FontChoice("pacifico", "Pacifico", "pacifico_regular"),
    FontChoice("caveat", "Caveat", "caveat_bold"),
    FontChoice("righteous", "Righteous", "righteous_regular"),
    FontChoice("sacramento", "Sacramento", "sacramento_regular"),
)

/** Latar dari aset gambar. [asset] null berarti pakai warna solid. */
data class BackgroundChoice(
    val id: String,
    val label: String,
    val asset: String?,
    val color: Color = Color(0xFF0A0A0F),
    val isTexture: Boolean = false,
)

val BundledBackgrounds: List<BackgroundChoice> = listOf(
    BackgroundChoice("solid", "Warna", null, Color(0xFF0A0A0F)),
    BackgroundChoice("ember", "Ember", "backgrounds/mesh_ember.webp"),
    BackgroundChoice("ocean", "Ocean", "backgrounds/mesh_ocean.webp"),
    BackgroundChoice("blush", "Blush", "backgrounds/mesh_blush.webp"),
    BackgroundChoice("noir", "Noir", "backgrounds/grad_noir.webp"),
    BackgroundChoice("sunset", "Sunset", "backgrounds/grad_sunset.webp"),
    BackgroundChoice("bokeh_w", "Bokeh W", "backgrounds/bokeh_warm.webp"),
    BackgroundChoice("bokeh_c", "Bokeh C", "backgrounds/bokeh_cool.webp"),
    BackgroundChoice("grain", "Grain", "backgrounds/grain_film.webp"),
    BackgroundChoice("paper", "Kertas", "textures/paper.webp", isTexture = true),
    BackgroundChoice("linen", "Linen", "textures/linen.webp", isTexture = true),
    BackgroundChoice("marble", "Marmer", "textures/marble.webp", isTexture = true),
    BackgroundChoice("denim", "Denim", "textures/denim.webp", isTexture = true),
    BackgroundChoice("concrete", "Beton", "textures/concrete.webp", isTexture = true),
    BackgroundChoice("wood", "Kayu", "textures/wood.webp", isTexture = true),
    BackgroundChoice("nebula", "Nebula", "textures/nebula.webp", isTexture = true),
    BackgroundChoice("frosted", "Kaca", "textures/frosted.webp", isTexture = true),
)

/** Sticker berbasis gambar dari assets/stickers_png. */
data class StickerAsset(
    val id: String,
    val label: String,
    val asset: String,
)

val BundledStickers: List<StickerAsset> = listOf(
    "heart" to "Hati",
    "star" to "Bintang",
    "sparkle" to "Kilau",
    "ring" to "Cincin",
    "ribbon" to "Pita",
    "burst" to "Ledakan",
    "cloud" to "Awan",
    "bolt" to "Kilat",
    "leaf" to "Daun",
    "drop" to "Tetes",
    "cube" to "Kubus",
    "balloon" to "Balon",
    "crown" to "Mahkota",
    "glasses" to "Kacamata",
    "flower" to "Bunga",
    "arrow" to "Panah",
    "shooting" to "Bintang Jatuh",
    "frame" to "Bingkai",
    "check" to "Centang",
    "broken" to "Hati Retak",
    "quote" to "Kutip",
    "triangle" to "Segitiga",
    "wave" to "Gelombang",
    "bling" to "Bling",
).map { (id, label) -> StickerAsset(id, label, "stickers_png/$id.png") }

/** Preset template: kombinasi layout + gaya siap pakai. */
data class TemplatePreset(
    val id: String,
    val label: String,
    val layoutId: String,
    val aspectRatio: Float,
    val backgroundAsset: String?,
    val borderThickness: Float,
    val borderColor: Color,
    val cornerRadius: Float,
    val outerPadding: Float,
    val caption: String = "",
    val captionFontRes: String = "",
    val captionColor: Color = Color.White,
    val captionSize: Float = 0.07f,
    val captionY: Float = 0.93f,
)

val BundledTemplates: List<TemplatePreset> = listOf(
    TemplatePreset(
        "polaroid_cream", "Polaroid Klasik", "pol1", 1f,
        "textures/paper.webp", 0.055f, Color(0xFFF2E9DC), 0.0f, 0.03f,
        "KENANGAN", "caveat_bold", Color(0xFF3A2E22), 0.085f, 0.90f,
    ),
    TemplatePreset(
        "mag_dark", "Majalah Gelap", "mag1", 4f / 5f,
        "textures/nebula.webp", 0.012f, Color(0xFF2A2A38), 0.02f, 0.02f,
        "", "", Color.White,
    ),
    TemplatePreset(
        "strip_film", "Strip Film", "strip4", 9f / 16f,
        "textures/concrete.webp", 0.03f, Color(0xFF16160E), 0.0f, 0.04f,
        "2026", "bebasneue_regular", Color(0xFFEDEDE5), 0.06f, 0.955f,
    ),
    TemplatePreset(
        "grid_clean", "Grid Bersih", "grid2", 1f,
        null, 0.016f, Color.White, 0.0f, 0.018f,
    ),
    TemplatePreset(
        "mag_ember", "Sorot Ember", "mag4", 4f / 5f,
        "backgrounds/mesh_ember.webp", 0.01f, Color(0xFFFFB347), 0.025f, 0.025f,
    ),
    TemplatePreset(
        "polaroid_duo", "Polaroid Duo", "pol2", 4f / 5f,
        "textures/linen.webp", 0.05f, Color(0xFFF7F2E8), 0.0f, 0.03f,
        "SUMMER", "sacramento_regular", Color(0xFF5A4A38), 0.07f, 0.88f,
    ),
    TemplatePreset(
        "kayu_rustic", "Rustic", "mag3", 1f,
        "textures/wood.webp", 0.022f, Color(0xFF2E1E12), 0.012f, 0.03f,
        "abadi", "lobster_regular", Color(0xFFF5E6D0), 0.08f, 0.93f,
    ),
    TemplatePreset(
        "marmer", "Marmer", "mag2", 4f / 5f,
        "textures/marble.webp", 0.014f, Color.White, 0.03f, 0.03f,
        "ELEGANT", "playfairdisplay_bold", Color(0xFF3A3A48), 0.055f, 0.94f,
    ),
    TemplatePreset(
        "ocean_biru", "Ocean", "g23", 4f / 5f,
        "backgrounds/mesh_ocean.webp", 0.012f, Color(0xFF7FD8FF), 0.02f, 0.02f,
    ),
    TemplatePreset(
        "denim_street", "Street", "a1", 1f,
        "textures/denim.webp", 0.018f, Color(0xFFEDEDE5), 0.0f, 0.025f,
        "STREET", "anton_regular", Color(0xFFEDEDE5), 0.075f, 0.94f,
    ),
    TemplatePreset(
        "frosted_glass", "Kaca Buram", "grid3", 1f,
        "textures/frosted.webp", 0.02f, Color(0xCCFFFFFF), 0.045f, 0.028f,
    ),
    TemplatePreset(
        "sunset_fade", "Sunset", "a2", 4f / 5f,
        "backgrounds/grad_sunset.webp", 0.014f, Color(0xFFFFC078), 0.015f, 0.022f,
        "senja", "pacifico_regular", Color(0xFFFFF3E0), 0.08f, 0.93f,
    ),
)
