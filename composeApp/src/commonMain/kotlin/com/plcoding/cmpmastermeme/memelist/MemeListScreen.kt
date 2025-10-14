@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.extended
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemeListScreenRoot(
    onNavigateToEditTemplateSelected: (MemeTemplate) -> Unit,
    viewModel: MemeListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MemeListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is MemeListAction.OnTemplateSelected -> onNavigateToEditTemplateSelected(action.template)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun MemeListScreen(
    state: MemeListState,
    onAction: (MemeListAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        floatingActionButton = {
            MemeFloatingActionButton(
                onClick = { onAction(MemeListAction.OnCreateNewMeme) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black,
                )
            }
        }
    ) {

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isCreatingNewMeme) {
            TemplateListSheetRoot(
                sheetState = sheetState,
                onMemeTemplateSelected = { onAction(MemeListAction.OnTemplateSelected(it)) },
                onDismiss = { onAction(MemeListAction.OnCancelNewMemeCreation) },
                memeTemplates = state.templates
            )
        }
    }
}

@Composable
private fun MemeFloatingActionButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = MaterialTheme.colorScheme.extended.buttonGradient
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        MemeListScreen(
            state = MemeListState(),
            onAction = {}
        )
    }
}