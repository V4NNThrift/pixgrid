package com.kiyoshi.pixgrid.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiyoshi.pixgrid.render.CompositeRenderer
import com.kiyoshi.pixgrid.ui.AppleBlue
import com.kiyoshi.pixgrid.ui.AppleIconButton
import com.kiyoshi.pixgrid.ui.CanvasPreview
import com.kiyoshi.pixgrid.ui.PixGridTheme
import com.kiyoshi.pixgrid.ui.launchPhotoPicker
import com.kiyoshi.pixgrid.ui.rememberPhotoPicker
import com.kiyoshi.pixgrid.vm.EditorViewModel
import com.kiyoshi.pixgrid.vm.Tool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixGridTheme {
                EditorScreen(onClose = { finish() })
            }
        }
    }
}

@Composable
fun EditorScreen(onClose: () -> Unit, vm: EditorViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var busy by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }

    val picker = rememberPhotoPicker { uris ->
        vm.addPhotos(uris.map { it.toString() })
    }

    fun renderNow(): android.graphics.Bitmap? = CompositeRenderer.render(
        context = context,
        layout = state.layout,
        slots = state.slots,
        canvasStyle = state.style,
        texts = state.texts,
        stickers = state.stickers,
        doodles = state.doodles,
        targetWidth = 1600,
    )

    fun doShare() {
        if (!state.slots.any { it != null }) {
            Toast.makeText(context, "Tambahkan foto dulu", Toast.LENGTH_SHORT).show()
            return
        }
        busy = true
        scope.launch {
            val file = withContext(Dispatchers.Default) {
                val bmp = renderNow() ?: return@withContext null
                CompositeRenderer.saveToCache(context, bmp)
            }
            busy = false
            if (file != null) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Bagikan kolase"))
            } else {
                Toast.makeText(context, "Gagal membuat gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun doSave() {
        if (!state.slots.any { it != null }) {
            Toast.makeText(context, "Tambahkan foto dulu", Toast.LENGTH_SHORT).show()
            return
        }
        busy = true
        scope.launch {
            val uri = withContext(Dispatchers.Default) {
                val bmp = renderNow() ?: return@withContext null
                CompositeRenderer.saveToGallery(context, bmp)
            }
            busy = false
            if (uri != null) {
                Toast.makeText(context, "Tersimpan ke Galeri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // ---- Navigation bar gaya iOS ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onClose) {
                    Text(
                        "Batal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Editor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { doShare() }) {
                    Text(
                        "Bagikan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                TextButton(onClick = { doSave() }) {
                    Text(
                        "Simpan",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // ---- Kanvas ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                CanvasPreview(
                    layout = state.layout,
                    slots = state.slots,
                    aspectRatio = state.style.aspectRatio,
                    background = state.style.background,
                    borderColor = state.style.border.color,
                    borderThickness = state.style.border.thickness,
                    cornerRadius = state.style.border.cornerRadius,
                    onCellClick = { idx ->
                        vm.selectCell(idx)
                        if (state.slots.getOrNull(idx) == null) {
                            launchPhotoPicker(picker)
                        } else {
                            vm.selectTool(Tool.PHOTO)
                        }
                    },
                )
            }

            // ---- Toolbar ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppleIconButton(
                    icon = Icons.Filled.Undo,
                    contentDescription = "Urungkan",
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = { vm.undo() },
                )
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(Tool.entries.toList()) { tool ->
                        ToolChip(
                            tool = tool,
                            active = state.tool == tool,
                            onClick = {
                                vm.selectTool(tool)
                                if (tool == Tool.PHOTO) launchPhotoPicker(picker)
                            },
                        )
                    }
                }
                AppleIconButton(
                    icon = Icons.Filled.Redo,
                    contentDescription = "Ulangi",
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = { vm.redo() },
                )
            }

            // ---- Panel bawah: sheet gaya macOS ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    )
                    .navigationBarsPadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    when (state.tool) {
                        Tool.TEMPLATE -> TemplatePanel(vm = vm)
                        Tool.PHOTO -> PhotoPanel(vm = vm, state = state, onPick = { launchPhotoPicker(picker) })
                        Tool.BORDER -> BorderPanel(vm = vm, state = state)
                        Tool.BACKGROUND -> BackgroundPanel(vm = vm, currentAsset = state.style.backgroundAsset)
                        Tool.FILTER -> FilterPanel(vm = vm, state = state)
                        Tool.TEXT -> {
                            TextPanel(vm = vm, state = state, onAdd = { showTextDialog = true })
                            val sel = state.texts.firstOrNull { it.id == state.selectedTextId }
                                ?: state.texts.lastOrNull()
                            Spacer(Modifier.height(16.dp))
                            FontPickerPanel(vm = vm, textId = sel?.id, currentFont = sel?.fontRes ?: "")
                        }
                        Tool.STICKER -> StickerAssetPanel(vm = vm)
                        Tool.DOODLE -> DoodlePanel(vm = vm, state = state)
                        Tool.FORMAT -> FormatPanel(vm = vm, state = state)
                        Tool.LAYOUT -> LayoutPanel(vm = vm, state = state)
                    }
                    Spacer(Modifier.height(24.dp))
                }

                if (busy) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppleBlue)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Membuat gambar…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTextDialog) {
        AlertDialog(
            onDismissRequest = { showTextDialog = false },
            title = { Text("Tambah teks") },
            text = {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Tulis sesuatu…") },
                    maxLines = 3,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.addText(textInput)
                    textInput = ""
                    showTextDialog = false
                }) { Text("Tambah") }
            },
            dismissButton = {
                TextButton(onClick = { showTextDialog = false }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun ToolChip(tool: Tool, active: Boolean, onClick: () -> Unit) {
    val tint = if (active) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    val icon = when (tool) {
        Tool.TEMPLATE -> Icons.Filled.Wallpaper
        Tool.BACKGROUND -> Icons.Filled.Brush
        Tool.LAYOUT -> Icons.Filled.GridView
        Tool.PHOTO -> Icons.Filled.Image
        Tool.BORDER -> Icons.Filled.CropSquare
        Tool.FILTER -> Icons.Filled.Tune
        Tool.TEXT -> Icons.Filled.TextFields
        Tool.STICKER -> Icons.Filled.EmojiEmotions
        Tool.DOODLE -> Icons.Filled.Brush
        Tool.FORMAT -> Icons.Filled.AspectRatio
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Icon(icon, tool.label, tint = tint, modifier = Modifier.size(19.dp))
        Spacer(Modifier.height(3.dp))
        Text(
            text = tool.label,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            maxLines = 1,
        )
    }
}

/** Label section gaya iOS grouped list. */
@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}