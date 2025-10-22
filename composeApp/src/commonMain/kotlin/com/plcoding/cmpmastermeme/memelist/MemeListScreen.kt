@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.title_your_memes
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.asString
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
                .padding(innerPadding)
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