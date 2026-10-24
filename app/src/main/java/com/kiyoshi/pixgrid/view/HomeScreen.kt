package com.kiyoshi.pixgrid.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kiyoshi.pixgrid.view.EditorActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiyoshi.pixgrid.model.Layouts
import com.kiyoshi.pixgrid.ui.Ink
import com.kiyoshi.pixgrid.ui.Ink3
import com.kiyoshi.pixgrid.ui.MiniLayout
import com.kiyoshi.pixgrid.ui.Mist

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var selectedLayoutId by remember { mutableStateOf<String?>(null) }
    val openEditor = { Intent(context, EditorActivity::class.java).also { context.startActivity(it) } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Column(modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 56.dp)) {
                Text(
                    text = "PixGrid",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(0xFFF2F2F5),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Kolase foto. Tanpa batas, tanpa watermark.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mist,
                )
            }

            Spacer(Modifier.height(28.dp))

            // Tombol utama
            Row(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PrimaryAction(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Filled.AutoAwesome, null, tint = Color(0xFF1A0A00)) },
                    title = "Buat Kolase",
                    subtitle = "Mulai dari kanvas kosong",
                    gradient = Brush.linearGradient(listOf(Color(0xFFFF7A45), Color(0xFFFFB347))),
                    onClick = openEditor,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SmallAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.GridView,
                    title = "Template",
                    subtitle = "${Layouts.all.size} layout",
                    onClick = openEditor,
                )
                SmallAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.PhotoLibrary,
                    title = "Galeri",
                    subtitle = "Hasil kolase",
                    onClick = openEditor,
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "MULAI CEPAT",
                style = MaterialTheme.typography.labelMedium,
                color = Mist,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(horizontal = 22.dp),
            )
            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(Layouts.all, key = { it.id }) { layout ->
                    val selected = selectedLayoutId == layout.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) Color(0xFF241820) else Ink3)
                            .clickable { selectedLayoutId = layout.id; openEditor() }
                            .padding(10.dp),
                    ) {
                        MiniLayout(layout = layout, active = selected)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = layout.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) Color(0xFFFF7A45) else Mist,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Dibuat oleh Kiyoshi · v1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3A3A48),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PrimaryAction(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    gradient: Brush,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(104.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center,
            ) { icon() }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2A0E00),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0x99331400),
            )
        }
    }
}

@Composable
private fun SmallAction(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Ink3)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFF242433)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color(0xFFFFB347), modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFEDEDF2),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Mist,
            )
        }
    }
}
