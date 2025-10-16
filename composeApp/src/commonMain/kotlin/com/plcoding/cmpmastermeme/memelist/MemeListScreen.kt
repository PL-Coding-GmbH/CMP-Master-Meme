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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.empty_meme
import cmpmastermeme.composeapp.generated.resources.meme_empty_list
import cmpmastermeme.composeapp.generated.resources.title_your_memes
import coil3.compose.AsyncImage
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.extended
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.BottomGradient
import com.plcoding.cmpmastermeme.core.presentation.asString
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when {
                state.memes.isEmpty() -> CreateFirstMemeNotice()
                else -> ListOfMemes(state.memes)
            }

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
}

@Composable
private fun CreateFirstMemeNotice() {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_meme),
            contentDescription = null
        )
        Text(
            text = stringResource(Res.string.meme_empty_list),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ListOfMemes(
    memes: List<MemeUi>
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 80.dp
            )
        ) {
            items(
                items = memes,
                key = { meme -> meme.imageUri }
            ) { meme ->
                Card(
                    modifier = Modifier.aspectRatio(1f),
                    onClick = { },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AsyncImage(
                        model = meme.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        BottomGradient()
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