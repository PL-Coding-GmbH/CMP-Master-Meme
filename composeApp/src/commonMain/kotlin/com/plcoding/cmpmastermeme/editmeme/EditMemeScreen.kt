@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.add_text
import cmpmastermeme.composeapp.generated.resources.save_meme
import cmpmastermeme.composeapp.generated.resources.title_new_meme
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.asString
import com.plcoding.cmpmastermeme.editmeme.components.MemePrimaryButton
import com.plcoding.cmpmastermeme.editmeme.components.MemeSecondaryButton
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditMemeScreenRoot(
    template: MemeTemplate,
    onGoBackClick: () -> Unit,
    viewModel: EditMemeViewModel = koinViewModel()
) {
    EditMemeScreen(
        template = template,
        onAction = { action ->
            when (action) {
                EditMemeAction.OnGoBackClick -> onGoBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun EditMemeScreen(
    template: MemeTemplate,
    onAction: (EditMemeAction) -> Unit,
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isSheetVisible) {
        SaveMemeContextSheetRoot(
            onSaveClick = {},
            onShareClick = {},
            onDismiss = { isSheetVisible = false },
            sheetState = sheetState,
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                ),
                title = {
                    Text(
                        text = Res.string.title_new_meme.asString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(EditMemeAction.OnGoBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End)
            ) {
                MemeSecondaryButton(
                    text = Res.string.add_text.asString(),
                    onClick = { onAction(EditMemeAction.OnAddTextToTemplateClick) }
                )
                MemePrimaryButton(
                    text = Res.string.save_meme.asString(),
                    onClick = { onAction(EditMemeAction.OnSaveMemeClick) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageResource(template.drawableResource),
                contentDescription = template.id,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        EditMemeScreen(
            template = MemeTemplate.TEMPLATE_02,
            onAction = {}
        )
    }
}