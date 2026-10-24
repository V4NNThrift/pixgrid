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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kiyoshi.pixgrid.ui.CanvasPreview
import com.kiyoshi.pixgrid.ui.Ink
import com.kiyoshi.pixgrid.ui.Ink2
import com.kiyoshi.pixgrid.ui.Ink3
import com.kiyoshi.pixgrid.ui.Mist
import com.kiyoshi.pixgrid.ui.Mist2
import com.kiyoshi.pixgrid.ui.PixGridTheme
import com.kiyoshi.pixgrid.ui.ToolButton
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

    fun doExport() {
        if (!state.slots.any { it != null }) {
            Toast.makeText(context, "Tambahkan foto dulu", Toast.LENGTH_SHORT).show()
            return
        }
        busy = true
        scope.launch {
            val bmp = withContext(Dispatchers.Default) {
                CompositeRenderer.render(
                    context = context,
                    layout = state.layout,
                    slots = state.slots,
                    canvasStyle = state.style,
                    texts = state.texts,
                    stickers = state.stickers,
                    doodles = state.doodles,
                    targetWidth = 1600,
                )
            }
            val saved = withContext(Dispatchers.IO) {
                CompositeRenderer.saveToCache(context, bmp)
            }
            busy = false
            if (saved != null) {
                val uri = Uri.fromFile(saved)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
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
            val bmp = withContext(Dispatchers.Default) {
                CompositeRenderer.render(
                    context = context,
                    layout = state.layout,
                    slots = state.slots,
                    canvasStyle = state.style,
                    texts = state.texts,
                    stickers = state.stickers,
                    doodles = state.doodles,
                    targetWidth = 1600,
                )
            }
            val uri = withContext(Dispatchers.IO) {
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

    Box(modifier = Modifier.fillMaxSize().background(Ink)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color(0xFFEDEDF2))
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Editor",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFEDEDF2),
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { vm.undo() }) {
                    Icon(Icons.Filled.Undo, "Urungkan", tint = Color(0xFFEDEDF2))
                }
                IconButton(onClick = { vm.redo() }) {
                    Icon(Icons.Filled.Redo, "Ulangi", tint = Color(0xFFEDEDF2))
                }
                IconButton(onClick = { doExport() }) {
                    Icon(Icons.Filled.Share, "Bagikan", tint = Color(0xFFFFB347))
                }
                IconButton(onClick = { doSave() }) {
                    Icon(Icons.Filled.Check, "Simpan", tint = Color(0xFFFF7A45))
                }
            }

            // Kanvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
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
                        val slot = state.slots.getOrNull(idx)
                        if (slot == null) {
                            launchPhotoPicker(picker)
                        } else {
                            vm.selectTool(Tool.PHOTO)
                        }
                    },
                )
            }

            Spacer(Modifier.height(14.dp))

            // Toolbar horizontal
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(Tool.entries.toList()) { tool ->
                    ToolButton(
                        icon = {
                            ToolIcon(tool = tool, active = state.tool == tool)
                        },
                        label = tool.label,
                        active = state.tool == tool,
                        onClick = {
                            vm.selectTool(tool)
                            if (tool == Tool.PHOTO) {
                                launchPhotoPicker(picker)
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Panel bawah sesuai tool
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Ink2)
                    .navigationBarsPadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    when (state.tool) {
                        Tool.PHOTO -> PhotoPanel(vm = vm, state = state, onPick = { launchPhotoPicker(picker) })
                        Tool.BORDER -> BorderPanel(vm = vm, state = state)
                        Tool.FILTER -> FilterPanel(vm = vm, state = state)
                        Tool.TEXT -> TextPanel(vm = vm, state = state, onAdd = { showTextDialog = true })
                        Tool.STICKER -> StickerPanel(vm = vm, state = state)
                        Tool.DOODLE -> DoodlePanel(vm = vm, state = state)
                        Tool.FORMAT -> FormatPanel(vm = vm, state = state)
                        Tool.LAYOUT -> LayoutPanel(vm = vm, state = state)
                    }
                    Spacer(Modifier.height(20.dp))
                }

                if (busy) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xCC0A0A0F)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFFFF7A45))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Merender kolase…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Mist,
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
                    singleLine = false,
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
private fun ToolIcon(tool: Tool, active: Boolean) {
    val tint = if (active) Color(0xFFFF7A45) else Mist
    val icon = when (tool) {
        Tool.LAYOUT -> Icons.Filled.GridView
        Tool.PHOTO -> Icons.Filled.Image
        Tool.BORDER -> Icons.Filled.CropSquare
        Tool.FILTER -> Icons.Filled.Tune
        Tool.TEXT -> Icons.Filled.TextFields
        Tool.STICKER -> Icons.Filled.EmojiEmotions
        Tool.DOODLE -> Icons.Filled.Brush
        Tool.FORMAT -> Icons.Filled.AspectRatio
    }
    Icon(icon, tool.label, tint = tint, modifier = Modifier.size(20.dp))
}

@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Mist2,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}
