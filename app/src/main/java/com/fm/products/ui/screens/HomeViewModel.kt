package com.fm.products.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.fm.products.ui.models.GraphicTool
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
        _uiState.update { it.copy(graphicTool = tool) }
    }

    fun changeProgressState(isProgress: Boolean) {
        _uiState.update {
            it.copy(isProgress = isProgress)
        }
    }

    data class UiState(
        val imageUri: Uri? = null,
        val graphicTool: GraphicTool = GraphicTool.None,
        var isProgress: Boolean = false,
    )
}
