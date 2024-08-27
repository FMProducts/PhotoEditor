package com.fm.products.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.models.ImageFilter
import com.fm.products.ui.models.OtherGraphicTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun setImageUri(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun updateSelectionTool(tool: GraphicTool) {
        if (tool is OtherGraphicTool.PhotoFilter && tool.selectedFilter == ImageFilter.None) {
            setFilterBottomSheetVisibility(true)
        }
        _uiState.update { it.copy(graphicTool = tool) }
    }

    fun changeProgressState(isProgress: Boolean) {
        _uiState.update {
            it.copy(isProgress = isProgress)
        }
    }

    fun setSelectToolsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update {
            it.copy(isShowSelectToolsBottomSheet = isVisible)
        }
    }

    fun setFilterBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update {
            it.copy(isShowFilterBottomSheet = isVisible)
        }
    }

    fun updateImageFilter(imageFilter: ImageFilter) {
        val filterTool = _uiState.value.graphicTool as? OtherGraphicTool.PhotoFilter
        val newFilterTool = filterTool?.copy(selectedFilter = imageFilter) ?: return
        _uiState.update {
            it.copy(graphicTool = newFilterTool)
        }
    }

    data class UiState(
        val imageUri: Uri? = null,
        val graphicTool: GraphicTool = GraphicTool.None,
        val isProgress: Boolean = false,
        val isShowSelectToolsBottomSheet: Boolean = false,
        val isShowFilterBottomSheet: Boolean = false,
    )
}
