package com.fm.products.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.fm.products.ui.models.SelectionTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun setImageUri(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun updateSelectionTool(tool: SelectionTool) {
        _uiState.update { it.copy(selectionTool = tool) }
    }

    data class UiState(
        val imageUri: Uri? = null,
        val selectionTool: SelectionTool = SelectionTool.None,
    )
}
