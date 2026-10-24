package com.kiyoshi.pixgrid.vm

import androidx.lifecycle.ViewModel
import com.kiyoshi.pixgrid.model.BorderStyle
import com.kiyoshi.pixgrid.model.CanvasStyle
import com.kiyoshi.pixgrid.model.Doodle
import com.kiyoshi.pixgrid.model.GridLayout
import com.kiyoshi.pixgrid.model.BundledTemplates
import com.kiyoshi.pixgrid.model.Layouts
import com.kiyoshi.pixgrid.model.TemplatePreset
import com.kiyoshi.pixgrid.model.PhotoSlot
import com.kiyoshi.pixgrid.model.StickerItem
import com.kiyoshi.pixgrid.model.TextItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class EditorState(
    val layout: GridLayout = Layouts.all[0],
    val slots: List<PhotoSlot?> = List(Layouts.all[0].count) { null },
    val style: CanvasStyle = CanvasStyle(),
    val texts: List<TextItem> = emptyList(),
    val stickers: List<StickerItem> = emptyList(),
    val doodles: List<Doodle> = emptyList(),
    val selectedCell: Int? = null,
    val selectedTextId: String? = null,
    val selectedStickerId: String? = null,
    val tool: Tool = Tool.LAYOUT,
    val showGrid: Boolean = false,
) {
    val filledCount: Int get() = slots.count { it != null }
    val isEmpty: Boolean get() = slots.all { it == null }
}

enum class Tool(val label: String) {
    TEMPLATE("Template"),
    LAYOUT("Layout"),
    PHOTO("Foto"),
    BORDER("Bingkai"),
    BACKGROUND("Latar"),
    FILTER("Filter"),
    TEXT("Teks"),
    STICKER("Sticker"),
    DOODLE("Coret"),
    FORMAT("Rasio"),
}

class EditorViewModel : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val history = ArrayDeque<EditorState>()
    private val future = ArrayDeque<EditorState>()

    private fun commit(transform: (EditorState) -> EditorState) {
        val current = _state.value
        history.addLast(current)
        if (history.size > 40) history.removeFirst()
        future.clear()
        _state.update(transform)
    }

    fun undo() {
        val prev = history.removeLastOrNull() ?: return
        future.addLast(_state.value)
        _state.value = prev
    }

    fun redo() {
        val next = future.removeLastOrNull() ?: return
        history.addLast(_state.value)
        _state.value = next
    }

    val canUndo: Boolean get() = history.isNotEmpty()
    val canRedo: Boolean get() = future.isNotEmpty()

    fun selectTool(tool: Tool) {
        _state.update { it.copy(tool = tool) }
    }

    fun selectCell(index: Int?) {
        _state.update { it.copy(selectedCell = index) }
    }

    fun setLayout(layout: GridLayout) {
        commit { st ->
            val newSlots = MutableList<PhotoSlot?>(layout.count) { i ->
                st.slots.getOrNull(i)
            }
            st.copy(
                layout = layout,
                slots = newSlots,
                selectedCell = null,
            )
        }
    }

    fun addPhotos(uris: List<String>) {
        if (uris.isEmpty()) return
        commit { st ->
            val slots = st.slots.toMutableList()
            var cursor = st.selectedCell?.takeIf { it in slots.indices && slots[it] == null }
                ?: slots.indexOfFirst { it == null }.takeIf { it >= 0 }
                ?: 0

            uris.forEach { uri ->
                if (cursor in slots.indices) {
                    slots[cursor] = PhotoSlot(uri = uri)
                    cursor++
                    while (cursor in slots.indices && slots[cursor] != null) cursor++
                }
            }
            st.copy(slots = slots, selectedCell = null)
        }
    }

    fun clearCell(index: Int) {
        commit { st ->
            val slots = st.slots.toMutableList()
            if (index in slots.indices) slots[index] = null
            st.copy(slots = slots)
        }
    }

    fun replaceCell(index: Int, uri: String) {
        commit { st ->
            val slots = st.slots.toMutableList()
            if (index in slots.indices) slots[index] = PhotoSlot(uri = uri)
            st.copy(slots = slots)
        }
    }

    fun updateSlot(index: Int, transform: (PhotoSlot) -> PhotoSlot) {
        val st = _state.value
        val slot = st.slots.getOrNull(index) ?: return
        commit { s ->
            val slots = s.slots.toMutableList()
            slots[index] = transform(slot)
            s.copy(slots = slots)
        }
    }

    /** Live update tanpa masuk history (untuk drag slider). */
    fun updateSlotLive(index: Int, transform: (PhotoSlot) -> PhotoSlot) {
        _state.update { s ->
            val slots = s.slots.toMutableList()
            slots.getOrNull(index)?.let { slots[index] = transform(it) }
            s.copy(slots = slots)
        }
    }

    fun pushHistory() {
        val current = _state.value
        history.addLast(current)
        if (history.size > 40) history.removeFirst()
        future.clear()
    }

    fun setBorder(transform: (BorderStyle) -> BorderStyle) {
        _state.update { st ->
            st.copy(style = st.style.copy(border = transform(st.style.border)))
        }
    }

    fun setBorderLive(transform: (BorderStyle) -> BorderStyle) = setBorder(transform)

    fun setStyle(transform: (CanvasStyle) -> CanvasStyle) {
        commit { st -> st.copy(style = transform(st.style)) }
    }

    fun setStyleLive(transform: (CanvasStyle) -> CanvasStyle) {
        _state.update { st -> st.copy(style = transform(st.style)) }
    }

    fun addText(text: String) {
        if (text.isBlank()) return
        commit { st ->
            st.copy(
                texts = st.texts + TextItem(
                    id = UUID.randomUUID().toString(),
                    text = text,
                ),
                selectedTextId = st.texts.lastOrNull()?.id,
            )
        }
    }

    fun updateText(id: String, transform: (TextItem) -> TextItem) {
        _state.update { st ->
            st.copy(texts = st.texts.map { if (it.id == id) transform(it) else it })
        }
    }

    fun removeText(id: String) {
        commit { st -> st.copy(texts = st.texts.filterNot { it.id == id }) }
    }

    fun selectText(id: String?) {
        _state.update { it.copy(selectedTextId = id) }
    }

    /** Terapkan preset template: layout, rasio, background, border, caption. */
    fun applyTemplate(preset: TemplatePreset) {
        val layout = Layouts.byId(preset.layoutId)
        commit { st ->
            val newSlots = MutableList<PhotoSlot?>(layout.count) { i ->
                st.slots.getOrNull(i)
            }
            st.copy(
                layout = layout,
                slots = newSlots,
                style = st.style.copy(
                    aspectRatio = preset.aspectRatio,
                    backgroundAsset = preset.backgroundAsset,
                    border = st.style.border.copy(
                        thickness = preset.borderThickness,
                        color = preset.borderColor,
                        cornerRadius = preset.cornerRadius,
                        outerPadding = preset.outerPadding,
                    ),
                ),
                texts = if (preset.caption.isBlank()) st.texts else st.texts + TextItem(
                    id = UUID.randomUUID().toString(),
                    text = preset.caption,
                    x = 0.5f,
                    y = preset.captionY,
                    size = preset.captionSize,
                    color = preset.captionColor,
                    fontRes = preset.captionFontRes,
                ),
                selectedCell = null,
            )
        }
    }

    /** Set background dari aset; null berarti balik ke warna solid. */
    fun setBackgroundAsset(asset: String?) {
        _state.update { st ->
            st.copy(style = st.style.copy(backgroundAsset = asset))
        }
    }

    fun addStickerAsset(asset: String) {
        commit { st ->
            st.copy(
                stickers = st.stickers + StickerItem(
                    id = UUID.randomUUID().toString(),
                    asset = asset,
                ),
                selectedStickerId = st.stickers.lastOrNull()?.id,
            )
        }
    }

    fun setTextFont(id: String, fontRes: String) {
        updateText(id) { it.copy(fontRes = fontRes) }
    }

    fun addSticker(glyph: String) {
        commit { st ->
            st.copy(
                stickers = st.stickers + StickerItem(
                    id = UUID.randomUUID().toString(),
                    glyph = glyph,
                ),
                selectedStickerId = st.stickers.lastOrNull()?.id,
            )
        }
    }

    fun updateSticker(id: String, transform: (StickerItem) -> StickerItem) {
        _state.update { st ->
            st.copy(stickers = st.stickers.map { if (it.id == id) transform(it) else it })
        }
    }

    fun removeSticker(id: String) {
        commit { st -> st.copy(stickers = st.stickers.filterNot { it.id == id }) }
    }

    fun selectSticker(id: String?) {
        _state.update { it.copy(selectedStickerId = id) }
    }

    fun startDoodle(point: Pair<Float, Float>) {
        commit { st ->
            st.copy(
                doodles = st.doodles + Doodle(
                    id = UUID.randomUUID().toString(),
                    points = listOf(point),
                )
            )
        }
    }

    fun extendDoodle(point: Pair<Float, Float>) {
        _state.update { st ->
            val last = st.doodles.lastOrNull() ?: return@update st
            st.copy(
                doodles = st.doodles.dropLast(1) +
                    last.copy(points = last.points + point)
            )
        }
    }

    fun clearDoodles() {
        commit { st -> st.copy(doodles = emptyList()) }
    }

    fun reset() {
        commit { EditorState() }
    }

    fun hasContent(): Boolean = _state.value.slots.any { it != null }
}
