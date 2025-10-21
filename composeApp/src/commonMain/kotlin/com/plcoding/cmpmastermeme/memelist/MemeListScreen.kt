@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.delete_meme_success
import cmpmastermeme.composeapp.generated.resources.empty_meme
import cmpmastermeme.composeapp.generated.resources.meme_empty_list
import cmpmastermeme.composeapp.generated.resources.title_your_memes
import coil3.compose.AsyncImage
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.extended
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.BottomGradient
import com.plcoding.cmpmastermeme.core.presentation.ObserveAsEvents
import com.plcoding.cmpmastermeme.core.presentation.asString
import com.plcoding.cmpmastermeme.editmeme.components.MemeUiAction
import com.plcoding.cmpmastermeme.editmeme.components.SaveMemeContextSheetRoot
import com.plcoding.cmpmastermeme.editmeme.components.confirmationdialog.DeleteMemeConfirmationDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemeListScreenRoot(
    onNavigateToEditTemplateSelected: (MemeTemplate) -> Unit,
    viewModel: MemeListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MemeListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = { action ->
            when (action) {
                is MemeListAction.OnTemplateSelected -> onNavigateToEditTemplateSelected(action.template)
                else -> Unit
            }
            viewModel.onAction(action)
        },
    )
}

@Composable
private fun MemeListScreen(
    state: MemeListState,
    snackbarHostState: SnackbarHostState,
    onAction: (MemeListAction) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Res.string.title_your_memes.asString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                ),
            )
        },
    ) { innerPadding ->
        MemeTemplateListContent(
            memeTemplates = state.templates,
            onMemeTemplateSelected = {
                onAction(MemeListAction.OnTemplateSelected(it))
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        MemeListScreen(
            state = MemeListState(),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}