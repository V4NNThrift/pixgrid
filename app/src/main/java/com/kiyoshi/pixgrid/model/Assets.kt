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

    // Mesh gradient
    BackgroundChoice("mesh_ember", "Ember", "backgrounds/mesh_ember.webp"),
    BackgroundChoice("mesh_ocean", "Ocean", "backgrounds/mesh_ocean.webp"),
    BackgroundChoice("mesh_blush", "Blush", "backgrounds/mesh_blush.webp"),
    BackgroundChoice("mesh_lavender", "Lavender", "backgrounds/mesh_lavender.webp"),
    BackgroundChoice("mesh_forest", "Forest", "backgrounds/mesh_forest.webp"),
    BackgroundChoice("mesh_peach", "Peach", "backgrounds/mesh_peach.webp"),
    BackgroundChoice("mesh_midnight", "Midnight", "backgrounds/mesh_midnight.webp"),
    BackgroundChoice("mesh_rose", "Rose", "backgrounds/mesh_rose.webp"),
    BackgroundChoice("mesh_mint", "Mint", "backgrounds/mesh_mint.webp"),
    BackgroundChoice("mesh_gold", "Gold", "backgrounds/mesh_gold.webp"),
    BackgroundChoice("mesh_steel", "Steel", "backgrounds/mesh_steel.webp"),

    // Linear gradient
    BackgroundChoice("grad_noir", "Noir", "backgrounds/grad_noir.webp"),
    BackgroundChoice("grad_sunset", "Sunset", "backgrounds/grad_sunset.webp"),
    BackgroundChoice("grad_dawn", "Dawn", "backgrounds/grad_dawn.webp"),
    BackgroundChoice("grad_sky", "Sky", "backgrounds/grad_sky.webp"),
    BackgroundChoice("grad_crimson", "Crimson", "backgrounds/grad_crimson.webp"),
    BackgroundChoice("grad_jade", "Jade", "backgrounds/grad_jade.webp"),
    BackgroundChoice("grad_honey", "Honey", "backgrounds/grad_honey.webp"),
    BackgroundChoice("grad_slate", "Slate", "backgrounds/grad_slate.webp"),

    // Bokeh
    BackgroundChoice("bokeh_warm", "Bokeh W", "backgrounds/bokeh_warm.webp"),
    BackgroundChoice("bokeh_cool", "Bokeh C", "backgrounds/bokeh_cool.webp"),
    BackgroundChoice("bokeh_ember", "Bokeh E", "backgrounds/bokeh_ember.webp"),
    BackgroundChoice("bokeh_azure", "Bokeh A", "backgrounds/bokeh_azure.webp"),
    BackgroundChoice("bokeh_green", "Bokeh G", "backgrounds/bokeh_green.webp"),
    BackgroundChoice("bokeh_rose", "Bokeh R", "backgrounds/bokeh_rose.webp"),

    // Grain
    BackgroundChoice("grain_film", "Grain", "backgrounds/grain_film.webp"),
    BackgroundChoice("grain_warm", "Grain W", "backgrounds/grain_warm.webp", isTexture = true),
    BackgroundChoice("grain_cool", "Grain C", "backgrounds/grain_cool.webp", isTexture = true),

    // Kertas
    BackgroundChoice("paper_white", "Kertas", "backgrounds/paper_white.webp", isTexture = true),
    BackgroundChoice("paper_kraft", "Kraft", "backgrounds/paper_kraft.webp", isTexture = true),
    BackgroundChoice("paper_black", "Kertas Gelap", "backgrounds/paper_black.webp", isTexture = true),
    BackgroundChoice("paper", "Paper Lama", "textures/paper.webp", isTexture = true),

    // Tekstur lain
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
    TemplatePreset(
        "lavender_dream", "Lavender", "mag2", 4f / 5f,
        "backgrounds/mesh_lavender.webp", 0.012f, Color(0xFFE8E0FF), 0.028f, 0.024f,
        "dream", "sacramento_regular", Color(0xFF3A2E5A), 0.09f, 0.93f,
    ),
    TemplatePreset(
        "forest_earth", "Forest", "a1", 1f,
        "backgrounds/mesh_forest.webp", 0.016f, Color(0xFF9FD8B8), 0.01f, 0.025f,
        "ALAM", "anton_regular", Color(0xFFE8F5EC), 0.07f, 0.94f,
    ),
    TemplatePreset(
        "peach_soft", "Peach", "pol2", 4f / 5f,
        "backgrounds/mesh_peach.webp", 0.045f, Color(0xFFFFF0E8), 0.0f, 0.028f,
        "manis", "pacifico_regular", Color(0xFF6A3A28), 0.075f, 0.90f,
    ),
    TemplatePreset(
        "midnight_glow", "Midnight", "mag1", 9f / 16f,
        "backgrounds/mesh_midnight.webp", 0.01f, Color(0xFF5090E0), 0.018f, 0.02f,
    ),
    TemplatePreset(
        "rose_gold", "Rose Gold", "grid2", 1f,
        "backgrounds/mesh_rose.webp", 0.014f, Color(0xFFFFD0DC), 0.022f, 0.024f,
        "bella", "playfairdisplay_bold", Color(0xFFFFF0F4), 0.055f, 0.935f,
    ),
    TemplatePreset(
        "mint_fresh", "Mint", "g23", 4f / 5f,
        "backgrounds/mesh_mint.webp", 0.012f, Color(0xFFB8F0E0), 0.02f, 0.022f,
    ),
    TemplatePreset(
        "gold_luxe", "Gold Luxe", "mag3", 4f / 5f,
        "backgrounds/mesh_gold.webp", 0.016f, Color(0xFFFFE0A0), 0.012f, 0.026f,
        "LUXE", "playfairdisplay_bold", Color(0xFFFFF8E0), 0.06f, 0.94f,
    ),
    TemplatePreset(
        "steel_urban", "Urban", "a2", 1f,
        "backgrounds/mesh_steel.webp", 0.014f, Color(0xFFC0C8D8), 0.008f, 0.022f,
        "URBAN", "oswald_bold", Color(0xFFEDF0F8), 0.07f, 0.94f,
    ),
    TemplatePreset(
        "dawn_soft", "Fajar", "strip3", 9f / 16f,
        "backgrounds/grad_dawn.webp", 0.028f, Color(0xFF3A1830), 0.0f, 0.035f,
        "fajar", "caveat_bold", Color(0xFFFFE8D8), 0.075f, 0.945f,
    ),
    TemplatePreset(
        "crimson_bold", "Crimson", "mag4", 4f / 5f,
        "backgrounds/grad_crimson.webp", 0.012f, Color(0xFFE06060), 0.016f, 0.024f,
        "BERANI", "anton_regular", Color(0xFFFFF0F0), 0.075f, 0.945f,
    ),
    TemplatePreset(
        "honey_warm", "Honey", "grid3", 1f,
        "backgrounds/grad_honey.webp", 0.012f, Color(0xFFFFE0A0), 0.024f, 0.026f,
        "hangat", "lobster_regular", Color(0xFFFFF8E8), 0.07f, 0.94f,
    ),
    TemplatePreset(
        "kraft_notebook", "Notebook", "pol1", 4f / 5f,
        "backgrounds/paper_kraft.webp", 0.05f, Color(0xFFE8D8B8), 0.0f, 0.03f,
        "catatan", "caveat_bold", Color(0xFF4A3420), 0.085f, 0.90f,
    ),
    TemplatePreset(
        "black_metal", "Metal", "mag1", 1f,
        "backgrounds/paper_black.webp", 0.014f, Color(0xFF6A6A78), 0.006f, 0.022f,
        "NOIR", "bebasneue_regular", Color(0xFFEDEDF5), 0.075f, 0.945f,
    ),
    TemplatePreset(
        "azure_cool", "Azure", "a1", 4f / 5f,
        "backgrounds/bokeh_azure.webp", 0.014f, Color(0xFFA0D0FF), 0.02f, 0.024f,
    ),
    TemplatePreset(
        "green_fresh", "Sage", "pol2", 1f,
        "backgrounds/bokeh_green.webp", 0.04f, Color(0xFFE8F8E8), 0.0f, 0.028f,
        "hijau", "righteous_regular", Color(0xFF2A4A2A), 0.07f, 0.90f,
    ),
    TemplatePreset(
        "rose_bokeh", "Rosé", "mag2", 4f / 5f,
        "backgrounds/bokeh_rose.webp", 0.014f, Color(0xFFFFD0E0), 0.024f, 0.024f,
        "rosé", "sacramento_regular", Color(0xFFFFF0F5), 0.085f, 0.935f,
    ),
)
