package com.kiyoshi.pixgrid.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kiyoshi.pixgrid.model.CellRect
import com.kiyoshi.pixgrid.model.GridLayout
import com.kiyoshi.pixgrid.model.PhotoSlot
import kotlin.math.roundToInt

/**
 * Kanvas preview dengan sel yang diposisikan secara fraksional.
 * Setiap sel menempati area [CellRect] relatif terhadap ukuran kanvas.
 */
@Composable
fun CanvasPreview(
    layout: GridLayout,
    slots: List<PhotoSlot?>,
    aspectRatio: Float,
    background: Color,
    borderColor: Color,
    borderThickness: Float,
    cornerRadius: Float,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
    ) {
        FractionalLayout(layout = layout, gap = borderThickness) {
            layout.cells.forEachIndexed { index, _ ->
                key(index) {
                    val slot = slots.getOrNull(index)
                    val radius = (cornerRadius * 400f).dp
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(radius))
                            .background(if (slot == null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
                            .border(
                                0.8.dp,
                                if (slot == null) MaterialTheme.colorScheme.outline else borderColor.copy(alpha = 0.3f),
                                RoundedCornerShape(radius)
                            )
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (slot != null) {
                            AsyncImage(
                                model = slot.uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Tambah foto",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Layout yang memposisikan anak sesuai CellRect dari [layout].
 * Gap (border dalam fraksi) diterapkan sebagai inset simetris.
 */
@Composable
private fun FractionalLayout(
    layout: GridLayout,
    gap: Float,
    content: @Composable () -> Unit,
) {
    Layout(content = content) { measurables, constraints ->
        val totalW = constraints.maxWidth
        val totalH = constraints.maxHeight
        val gapPx = (gap * minOf(totalW, totalH)).roundToInt().coerceIn(0, 24)

        val placeables = measurables.mapIndexed { index, measurable ->
            val cell = layout.cells.getOrNull(index) ?: CellRect(0f, 0f, 0f, 0f)
            val cw = (cell.w * totalW).roundToInt() - gapPx
            val ch = (cell.h * totalH).roundToInt() - gapPx
            measurable.measure(
                androidx.compose.ui.unit.Constraints(
                    maxWidth = cw.coerceAtLeast(1),
                    maxHeight = ch.coerceAtLeast(1)
                )
            )
        }

        layout(
            width = totalW,
            height = totalH,
        ) {
            placeables.forEachIndexed { index, placeable ->
                val cell = layout.cells.getOrNull(index) ?: return@forEachIndexed
                val cx = (cell.x * totalW).roundToInt() + gapPx / 2
                val cy = (cell.y * totalH).roundToInt() + gapPx / 2
                placeable.placeRelative(cx, cy)
            }
        }
    }
}

/** Grid pemilih layout, bisa di-scroll horizontal. */
@Composable
fun LayoutPicker(
    layouts: List<GridLayout>,
    selectedId: String,
    onSelect: (GridLayout) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(layouts, key = { it.id }) { layout ->
            val selected = layout.id == selectedId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(74.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        if (selected) 1.5.dp else 0.dp,
                        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(layout) }
                    .padding(8.dp)
            ) {
                MiniLayout(layout = layout, active = selected)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = layout.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun MiniLayout(layout: GridLayout, active: Boolean) {
    val tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        FractionalLayout(layout = layout, gap = 0.02f) {
            layout.cells.forEach { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tint.copy(alpha = 0.55f), RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

/** Tombol bulat ikon dengan label di bawah. */
@Composable
fun ToolButton(
    icon: @Composable () -> Unit,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (active) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

/** Pemilih foto multi-select. */
@Composable
fun rememberPhotoPicker(onPicked: (List<Uri>) -> Unit) =
    rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) { uris -> onPicked(uris) }

fun launchPhotoPicker(
    launcher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>,
) {
    launcher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
}
