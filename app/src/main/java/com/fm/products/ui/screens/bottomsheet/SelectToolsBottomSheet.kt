@file:OptIn(ExperimentalMaterial3Api::class)

package com.fm.products.ui.screens.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.theme.PhotoEditorTheme

@Composable
fun SelectToolsBottomSheet(
    onDismissRequest: () -> Unit,
    onSelectTool: (GraphicTool) -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = state,
        windowInsets = WindowInsets.ime
    ) {
        SelectToolsBottomSheetContent(
            onSelectTool = {
                onSelectTool(it)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun SelectToolsBottomSheetContent(
    onSelectTool: (GraphicTool) -> Unit,
) {
    val tools = GraphicTool.values()

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 42.dp),
        contentPadding = PaddingValues(24.dp)
    ) {

        items(tools) {
            SelectToolItem(
                tool = it,
                onClick = onSelectTool
            )
        }
    }
}

@Composable
private fun SelectToolItem(
    tool: GraphicTool,
    onClick: (GraphicTool) -> Unit
) {
    Button(
        onClick = { onClick(tool) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Icon(
                painter = painterResource(id = tool.iconRes),
                contentDescription = "",
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(id = tool.nameRes),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SelectToolsBottomSheetPreview() {
    PhotoEditorTheme {
        SelectToolsBottomSheetContent(
            onSelectTool = { /* no-op */ },
        )
    }
}
