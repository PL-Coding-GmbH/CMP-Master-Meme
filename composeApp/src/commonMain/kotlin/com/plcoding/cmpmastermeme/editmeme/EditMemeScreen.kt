@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.add_text
import cmpmastermeme.composeapp.generated.resources.save_meme
import cmpmastermeme.composeapp.generated.resources.title_new_meme
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.asString
import com.plcoding.cmpmastermeme.editmeme.components.MemePrimaryButton
import com.plcoding.cmpmastermeme.editmeme.components.MemeSecondaryButton
import com.plcoding.cmpmastermeme.editmeme.components.MemeTextBox
import com.plcoding.cmpmastermeme.editmeme.components.SaveMemeContextSheetRoot
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun EditMemeScreenRoot(
    template: MemeTemplate,
    onGoBackClick: () -> Unit,
    viewModel: EditMemeViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    EditMemeScreen(
        template = template,
        state = state,
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
    state: EditMemeState,
    template: MemeTemplate,
    onAction: (EditMemeAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (state.isFinalisingMeme) {
        SaveMemeContextSheetRoot(
            onSaveClick = { onAction(EditMemeAction.OnSaveMemeClick(memeTemplate = template)) },
            onShareClick = { onAction(EditMemeAction.OnShareMemeClick(memeTemplate = template)) },
            onDismiss = {
                onAction(EditMemeAction.OnContinueEditing)
            },
            sheetState = sheetState,
        )
    }

    Scaffold(
        topBar = {
            TopBar(onGoBackClick = { onAction(EditMemeAction.OnGoBackClick) })
        },
        bottomBar = {
            BottomBar(
                onSaveMemeClick = { onAction(EditMemeAction.OnCompleteEditingClick) },
                onAddTextClick = { onAction(EditMemeAction.OnAddNewMemeTextClick) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box {
                Image(
                    bitmap = imageResource(template.drawableResource),
                    contentDescription = template.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPlaced { layoutCoordinates ->
                            val size = layoutCoordinates.size
                            onAction(
                                EditMemeAction.OnContainerSizeChanged(
                                    IntSize(size.width, size.height)
                                )
                            )
                        },
                    contentScale = ContentScale.FillWidth
                )
                DraggableContainer(
                    children = state.memeTexts,
                    selectedChildId = state.selectedTextBoxId,
                    editingChildId = state.editingTextBoxId,
                    onAction = onAction,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun DraggableContainer(
    modifier: Modifier = Modifier,
    children: List<MemeText>,
    selectedChildId: Int?,
    editingChildId: Int?,
    onAction: (EditMemeAction) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .border(4.dp, Color.Gray)
    ) {
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight
        println("kai parent width and height $parentWidth x $parentHeight")


        children.forEach { child ->
            var offsetX by remember(child.id) { mutableStateOf(child.offset.x) }
            var offsetY by remember(child.id) { mutableStateOf(child.offset.y) }
            var childWidth by remember { mutableStateOf(0) }
            var childHeight by remember { mutableStateOf(0) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .onPlaced { coordinates ->
                        // Get the actual size of the child
                        childWidth = coordinates.size.width
                        childHeight = coordinates.size.height

                        // Ensure child stays within bounds after measurement
                        val maxX = (parentWidth - childWidth).coerceAtLeast(0).toFloat()
                        val maxY = (parentHeight - childHeight).coerceAtLeast(0).toFloat()

                        offsetX = offsetX.coerceIn(0f, maxX)
                        offsetY = offsetY.coerceIn(0f, maxY)

                        onAction(
                            EditMemeAction.OnMemeTextPositionChange(
                                id = child.id,
                                x = offsetX,
                                y = offsetY
                            )
                        )
                    }
                    .pointerInput(child.id, selectedChildId) {
                        detectDragGestures { _, dragAmount ->
                            if (selectedChildId == child.id) {
                                val newX = offsetX + dragAmount.x
                                val newY = offsetY + dragAmount.y

                                // Constrain to parent bounds
                                val maxX = (parentWidth - childWidth).coerceAtLeast(0).toFloat()
                                val maxY = (parentHeight - childHeight).coerceAtLeast(0).toFloat()

                                offsetX = newX.coerceIn(0f, maxX)
                                offsetY = newY.coerceIn(0f, maxY)
                            }
                        }
                    }
            ) {
                MemeTextBox(
                    memeText = child,
                    modifier = Modifier.widthIn(max = (parentWidth * 0.5f).dp),
                    isSelected = child.id == selectedChildId,
                    isEditing = child.id == editingChildId,
                    onTextInputChange = {
                        onAction(
                            EditMemeAction.OnMemeTextChange(
                                id = child.id,
                                text = it
                            )
                        )
                    },
                    onDelete = { onAction(EditMemeAction.OnDeleteMemeText(child.id)) },
                    onClick = { onAction(EditMemeAction.OnSelectMemeText(child.id)) },
                    onDoubleClick = { onAction(EditMemeAction.OnEditMemeText(child.id)) }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onGoBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
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
            IconButton(onClick = onGoBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onAddTextClick: () -> Unit,
    onSaveMemeClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End)
    ) {
        MemeSecondaryButton(
            text = Res.string.add_text.asString(),
            onClick = onAddTextClick
        )
        MemePrimaryButton(
            text = Res.string.save_meme.asString(),
            onClick = onSaveMemeClick
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        EditMemeScreen(
            template = MemeTemplate.TEMPLATE_02,
            onAction = {},
            state = EditMemeState(
                memeTexts = listOf(
                    MemeText(
                        id = 0,
                        text = "Text #1",
                    ),
                    MemeText(
                        id = 1,
                        text = "Text #2",
                    )
                )
            )
        )
    }
}