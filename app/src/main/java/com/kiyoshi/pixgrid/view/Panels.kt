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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiyoshi.pixgrid.filter.Filters
import com.kiyoshi.pixgrid.model.AspectPresets
import com.kiyoshi.pixgrid.model.Doodle
import com.kiyoshi.pixgrid.model.Layouts
import com.kiyoshi.pixgrid.ui.Ink3
import com.kiyoshi.pixgrid.ui.Ink4
import com.kiyoshi.pixgrid.ui.LayoutPicker
import com.kiyoshi.pixgrid.ui.Mist
import com.kiyoshi.pixgrid.ui.Mist2
import com.kiyoshi.pixgrid.vm.EditorState
import com.kiyoshi.pixgrid.vm.EditorViewModel
import com.kiyoshi.pixgrid.vm.Tool

@Composable
fun LayoutPanel(vm: EditorViewModel, state: EditorState) {
    Column {
        SectionLabel("PILIH LAYOUT")
        LayoutPicker(
            layouts = Layouts.all,
            selectedId = state.layout.id,
            onSelect = { vm.setLayout(it) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun PhotoPanel(vm: EditorViewModel, state: EditorState, onPick: () -> Unit) {
    Column {
        SectionLabel("FOTO")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onPick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7A45),
                    contentColor = Color(0xFF1A0A00),
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tambah foto")
            }

            val idx = state.selectedCell
            if (idx != null && state.slots.getOrNull(idx) != null) {
                OutlinedButton(
                    onClick = { vm.clearCell(idx) },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Hapus sel")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val idx = state.selectedCell
        val slot = idx?.let { state.slots.getOrNull(it) }

        if (idx == null || slot == null) {
            Text(
                "Ketuk salah satu sel di kanvas untuk mengatur fotonya.",
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
            )
            return
        }

        SectionLabel("SEL ${idx + 1}")

        // Rotate & flip
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionChip(
                label = "Putar 90°",
                icon = Icons.Filled.Rotate90DegreesCcw,
                onClick = {
                    vm.updateSlot(idx) { it.copy(rotation = (it.rotation + 90f) % 360f) }
                }
            )
            ActionChip(
                label = "Balik",
                icon = Icons.Filled.Flip,
                onClick = {
                    vm.updateSlot(idx) { it.copy(flipH = !it.flipH) }
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Zoom
        SliderRow(
            label = "Zoom",
            value = slot.scale,
            range = 1f..3f,
            onValueChange = { v ->
                vm.updateSlotLive(idx) { it.copy(scale = v) }
            },
            onValueChangeFinished = { vm.pushHistory() },
        )

        // Crop geser
        SliderRow(
            label = "Geser horizontal",
            value = slot.offsetX,
            range = -0.5f..0.5f,
            onValueChange = { v -> vm.updateSlotLive(idx) { it.copy(offsetX = v) } },
            onValueChangeFinished = { vm.pushHistory() },
        )
        SliderRow(
            label = "Geser vertikal",
            value = slot.offsetY,
            range = -0.5f..0.5f,
            onValueChange = { v -> vm.updateSlotLive(idx) { it.copy(offsetY = v) } },
            onValueChangeFinished = { vm.pushHistory() },
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { vm.updateSlot(idx) { it.copy(scale = 1f, offsetX = 0f, offsetY = 0f, rotation = 0f, flipH = false, flipV = false) } },
            shape = RoundedCornerShape(12.dp),
        ) { Text("Reset posisi") }
    }
}

@Composable
fun BorderPanel(vm: EditorViewModel, state: EditorState) {
    val border = state.style.border
    Column {
        SectionLabel("BINGKAI")

        SliderRow(
            label = "Ketebalan",
            value = border.thickness,
            range = 0f..0.06f,
            onValueChange = { v -> vm.setBorderLive { it.copy(thickness = v) } },
            onValueChangeFinished = { vm.pushHistory() },
        )
        SliderRow(
            label = "Sudut melengkung",
            value = border.cornerRadius,
            range = 0f..0.06f,
            onValueChange = { v -> vm.setBorderLive { it.copy(cornerRadius = v) } },
            onValueChangeFinished = { vm.pushHistory() },
        )
        SliderRow(
            label = "Margin luar",
            value = border.outerPadding,
            range = 0f..0.08f,
            onValueChange = { v -> vm.setBorderLive { it.copy(outerPadding = v) } },
            onValueChangeFinished = { vm.pushHistory() },
        )

        Spacer(Modifier.height(14.dp))
        SectionLabel("WARNA BINGKAI")
        ColorRow(
            selected = border.color,
            onSelect = { c -> vm.setBorder { it.copy(color = c) } },
        )

        Spacer(Modifier.height(14.dp))
        SectionLabel("WARNA LATAR")
        ColorRow(
            selected = state.style.background,
            onSelect = { c -> vm.setStyle { it.copy(background = c) } },
        )
    }
}

@Composable
fun FilterPanel(vm: EditorViewModel, state: EditorState) {
    val idx = state.selectedCell
    Column {
        SectionLabel("FILTER")

        if (idx == null) {
            Text(
                "Ketuk sel di kanvas dulu, lalu pilih filter.",
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
            )
        } else {
            val current = state.slots.getOrNull(idx)?.filterId ?: "none"
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Filters.all, key = { it.id }) { f ->
                    val active = f.id == current
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) Color(0xFF3A1A08) else Ink3)
                            .border(
                                if (active) 1.5.dp else 0.dp,
                                if (active) Color(0xFFFF7A45) else Color.Transparent,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable {
                                vm.updateSlot(idx) { it.copy(filterId = f.id) }
                            }
                            .padding(8.dp),
                    ) {
                        Text(
                            text = f.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) Color(0xFFFFB347) else Mist,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            val strength = state.slots.getOrNull(idx)?.filterStrength ?: 1f
            SliderRow(
                label = "Kekuatan filter",
                value = strength,
                range = 0f..1f,
                onValueChange = { v -> vm.updateSlotLive(idx) { it.copy(filterStrength = v) } },
                onValueChangeFinished = { vm.pushHistory() },
            )
        }
    }
}

@Composable
fun TextPanel(
    vm: EditorViewModel,
    state: EditorState,
    onAdd: () -> Unit,
) {
    Column {
        SectionLabel("TEKS")

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF7A45),
                contentColor = Color(0xFF1A0A00),
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Tambah teks")
        }

        if (state.texts.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            SectionLabel("DAFTAR TEKS")
            state.texts.forEach { t ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Ink3)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = t.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEDEDF2),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Icon(
                        Icons.Filled.Delete,
                        "Hapus",
                        tint = Mist2,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { vm.removeText(t.id) },
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StickerPanel(vm: EditorViewModel, state: EditorState) {
    val stickers = listOf(
        "✨", "❤️", "😍", "🔥", "⭐", "🌸", "🎉", "💫",
        "🌻", "🍀", "☀️", "🌙", "⚡", "💎", "🎈", "🦋",
        "😎", "🥰", "👑", "🍕", "☕", "🎵", "📸", "🌈",
    )
    Column {
        SectionLabel("STICKER")
        val rows = stickers.chunked(8)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { glyph ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Ink3)
                            .clickable { vm.addSticker(glyph) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = glyph, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (state.stickers.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            TextButtonRow(
                label = "Hapus semua sticker (${state.stickers.size})",
                onClick = { state.stickers.forEach { vm.removeSticker(it.id) } },
            )
        }
    }
}

@Composable
fun DoodlePanel(vm: EditorViewModel, state: EditorState) {
    Column {
        SectionLabel("CORET")
        Text(
            "Gores di kanvas untuk mencoret. Fitur coret aktif saat panel ini terbuka.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mist,
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (state.doodles.isNotEmpty()) {
                OutlinedButton(
                    onClick = { vm.clearDoodles() },
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Hapus semua coretan") }
            }
        }
    }
}

@Composable
fun FormatPanel(vm: EditorViewModel, state: EditorState) {
    Column {
        SectionLabel("RASIO KANVAS")

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AspectPresets.all) { (label, ratio) ->
                val active = kotlin.math.abs(state.style.aspectRatio - ratio) < 0.001f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) Color(0xFF3A1A08) else Ink3)
                        .border(
                            if (active) 1.5.dp else 0.dp,
                            if (active) Color(0xFFFF7A45) else Color.Transparent,
                            RoundedCornerShape(12.dp),
                        )
                        .clickable { vm.setStyle { it.copy(aspectRatio = ratio) } }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (active) Color(0xFFFFB347) else Mist,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        SectionLabel("LATAR")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionChip(
                label = if (state.style.backgroundBlur) "Blur: ON" else "Blur: OFF",
                icon = Icons.Filled.Check,
                onClick = {
                    vm.setStyle { it.copy(backgroundBlur = !it.backgroundBlur) }
                },
            )
        }
    }
}

/* ---------- Komponen kecil yang dipakai panel ---------- */

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
            )
            Text(
                text = String.format("%.2f", value),
                style = MaterialTheme.typography.labelMedium,
                color = Mist2,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF7A45),
                activeTrackColor = Color(0xFFFF7A45),
                inactiveTrackColor = Ink4,
            ),
        )
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Ink3)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Color(0xFFFFB347), modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFEDEDF2),
        )
    }
}

@Composable
private fun TextButtonRow(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Ink3)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFFF7A45),
        )
    }
}

@Composable
private fun ColorRow(
    selected: Color,
    onSelect: (Color) -> Unit,
) {
    val palette = listOf(
        Color.White,
        Color(0xFF0A0A0F),
        Color(0xFFFF7A45),
        Color(0xFFFFB347),
        Color(0xFF8A7CFF),
        Color(0xFF3ECF8E),
        Color(0xFFFF5A5A),
        Color(0xFFF2E9DC),
        Color(0xFF1E3A5F),
        Color(0xFF4A2E1F),
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(palette) { c ->
            val active = c == selected
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        if (active) 2.5.dp else 1.dp,
                        if (active) Color(0xFFFF7A45) else Ink4,
                        CircleShape,
                    )
                    .clickable { onSelect(c) },
                contentAlignment = Alignment.Center,
            ) {
                if (active) {
                    Icon(
                        Icons.Filled.Check,
                        null,
                        tint = if (c.luminance() > 0.5f) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

private fun Color.luminance(): Float =
    0.2126f * red + 0.7152f * green + 0.0722f * blue
