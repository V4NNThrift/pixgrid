package com.kiyoshi.pixgrid.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kiyoshi.pixgrid.model.BundledTemplates
import com.kiyoshi.pixgrid.model.Layouts
import com.kiyoshi.pixgrid.ui.AppleCard
import com.kiyoshi.pixgrid.ui.AppleGreen
import com.kiyoshi.pixgrid.ui.AppleListRow
import com.kiyoshi.pixgrid.ui.AppleOrange
import com.kiyoshi.pixgrid.ui.ApplePink
import com.kiyoshi.pixgrid.ui.AppleSegmentedControl
import com.kiyoshi.pixgrid.ui.AppleSeparator
import com.kiyoshi.pixgrid.ui.MiniLayout

private enum class HomeTab { Template, Layout }

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(HomeTab.Template) }

    val openEditor: () -> Unit = {
        context.startActivity(Intent(context, EditorActivity::class.java))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Large title — pola iOS
        Column(
            modifier = Modifier.padding(
                start = 20.dp, end = 20.dp, top = 60.dp, bottom = 16.dp
            )
        ) {
            Text(
                text = "PixGrid",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Kolase foto",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Kartu aksi gaya Settings macOS
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AppleCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    AppleListRow(
                        icon = Icons.Filled.Star,
                        iconTint = AppleOrange,
                        title = "Buat Kolase Baru",
                        subtitle = "Kanvas kosong, atur sesukamu",
                        onClick = openEditor,
                    )
                    AppleSeparator(startIndent = 41)
                    AppleListRow(
                        icon = Icons.Filled.PhotoLibrary,
                        iconTint = AppleGreen,
                        title = "Pilih Template",
                        subtitle = "${BundledTemplates.size} preset siap pakai",
                        onClick = openEditor,
                    )
                    AppleSeparator(startIndent = 41)
                    AppleListRow(
                        icon = Icons.Filled.Collections,
                        iconTint = ApplePink,
                        title = "Hasil Kolase",
                        subtitle = "Tersimpan di Galeri",
                        onClick = openEditor,
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AppleSegmentedControl(
                items = listOf(
                    HomeTab.Template to "Template",
                    HomeTab.Layout to "Layout",
                ),
                selected = tab,
                onSelect = { tab = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(14.dp))

        when (tab) {
            HomeTab.Template -> TemplateRow(onClick = openEditor)
            HomeTab.Layout -> LayoutRow(onClick = openEditor)
        }

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "PixGrid 1.1.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${Layouts.all.size} layout · ${BundledTemplates.size} template · 89 latar",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun TemplateRow(onClick: () -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(BundledTemplates, key = { it.id }) { preset ->
            Column(
                modifier = Modifier
                    .width(124.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onClick)
                    .padding(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    val bg = preset.backgroundAsset
                    if (bg != null) {
                        AsyncImage(
                            model = "file:///android_asset/$bg",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize().padding(9.dp)) {
                        MiniLayout(layout = Layouts.byId(preset.layoutId), active = false)
                    }
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = preset.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun LayoutRow(onClick: () -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(Layouts.all, key = { it.id }) { layout ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onClick)
                    .padding(9.dp),
            ) {
                MiniLayout(layout = layout, active = false)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = layout.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}