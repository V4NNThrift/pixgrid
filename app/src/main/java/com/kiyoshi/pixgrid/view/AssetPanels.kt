package com.kiyoshi.pixgrid.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kiyoshi.pixgrid.R
import com.kiyoshi.pixgrid.model.BundledBackgrounds
import com.kiyoshi.pixgrid.model.BundledFonts
import com.kiyoshi.pixgrid.model.AllStickers
import com.kiyoshi.pixgrid.model.BundledStickers
import com.kiyoshi.pixgrid.model.BundledStickers2
import com.kiyoshi.pixgrid.model.BundledTemplates
import com.kiyoshi.pixgrid.vm.EditorViewModel

/** Pilih template siap pakai. */
@Composable
fun TemplatePanel(vm: EditorViewModel) {
    Column {
        SectionLabel("TEMPLATE SIAP PAKAI")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(BundledTemplates, key = { it.id }) { preset ->
                Column(
                    modifier = Modifier
                        .width(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { vm.applyTemplate(preset) }
                        .padding(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        val bg = preset.backgroundAsset
                        if (bg != null) {
                            AsyncImage(
                                model = "file:///android_asset/$bg",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(84.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Pilih latar: warna solid, gradient, atau texture. */
@Composable
fun BackgroundPanel(vm: EditorViewModel, currentAsset: String?) {
    Column {
        SectionLabel("LATAR")

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(BundledBackgrounds, key = { it.id }) { bg ->
                val active = if (bg.asset == null) currentAsset == null else currentAsset == bg.asset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(74.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            if (active) 1.5.dp else 0.dp,
                            if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(12.dp),
                        )
                        .clickable { vm.setBackgroundAsset(bg.asset) }
                        .padding(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg.color),
                    ) {
                        if (bg.asset != null) {
                            AsyncImage(
                                model = "file:///android_asset/${bg.asset}",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = bg.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Sticker dari aset PNG. */
@Composable
fun StickerAssetPanel(vm: EditorViewModel) {
    Column {
        SectionLabel("STICKER · ${AllStickers.size} PILIHAN")
        val rows = BundledStickers.chunked(6)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { st ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { vm.addStickerAsset(st.asset) },
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = "file:///android_asset/${st.asset}",
                            contentDescription = st.label,
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
                repeat(6 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(10.dp))
        SectionLabel("GARIS & IKON")
        BundledStickers2.chunked(6).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { st ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { vm.addStickerAsset(st.asset) },
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = "file:///android_asset/${st.asset}",
                            contentDescription = st.label,
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
                repeat(6 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

/** Pilih font untuk teks terpilih. */
@Composable
fun FontPickerPanel(
    vm: EditorViewModel,
    textId: String?,
    currentFont: String,
) {
    Column {
        SectionLabel("FONT")
        if (textId == null) {
            Text(
                "Tambahkan teks dulu, lalu pilih font.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(BundledFonts, key = { it.id }) { f ->
                val active = f.resName == currentFont
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            if (active) 1.5.dp else 0.dp,
                            if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(12.dp),
                        )
                        .clickable { vm.setTextFont(textId, f.resName) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = f.label,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = fontStyleFor(f.resName),
                    )
                }
            }
        }
    }
}

private fun fontStyleFor(resName: String): TextStyle {
    if (resName.isBlank()) return TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    return try {
        val id = when (resName) {
            "poppins_bold" -> R.font.poppins_bold
            "poppins_regular" -> R.font.poppins_regular
            "bebasneue_regular" -> R.font.bebasneue_regular
            "anton_regular" -> R.font.anton_regular
            "oswald_bold" -> R.font.oswald_bold
            "montserrat_bold" -> R.font.montserrat_bold
            "playfairdisplay_bold" -> R.font.playfairdisplay_bold
            "lobster_regular" -> R.font.lobster_regular
            "pacifico_regular" -> R.font.pacifico_regular
            "caveat_bold" -> R.font.caveat_bold
            "righteous_regular" -> R.font.righteous_regular
            "sacramento_regular" -> R.font.sacramento_regular
            else -> return TextStyle(fontSize = 15.sp)
        }
        TextStyle(fontFamily = FontFamily(Font(id)), fontSize = 16.sp)
    } catch (t: Throwable) {
        TextStyle(fontSize = 15.sp)
    }
}
