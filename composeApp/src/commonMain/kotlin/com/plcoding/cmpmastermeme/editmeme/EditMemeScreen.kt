@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
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
import com.plcoding.cmpmastermeme.core.presentation.ObserveAsEvents
import com.plcoding.cmpmastermeme.core.presentation.asString
import com.plcoding.cmpmastermeme.editmeme.components.FontSlider
import com.plcoding.cmpmastermeme.editmeme.components.MemePrimaryButton
import com.plcoding.cmpmastermeme.editmeme.components.MemeSecondaryButton
import com.plcoding.cmpmastermeme.editmeme.components.MemeTextBox
import com.plcoding.cmpmastermeme.editmeme.components.MemeUiAction
import com.plcoding.cmpmastermeme.editmeme.components.SaveMemeContextSheetRoot
import com.plcoding.cmpmastermeme.editmeme.components.confirmationdialog.LeaveEditorConfirmationDialog
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeEvent
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import com.plcoding.cmpmastermeme.editmeme.models.TextBoxInteractionState
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun EditMemeScreenRoot(
    template: MemeTemplate,
    navigateBack: () -> Unit,
    viewModel: EditMemeViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EditMemeEvent.SavedMeme,
            EditMemeEvent.ConfirmedLeaveWithoutSaving -> navigateBack()
        }
    }

    EditMemeScreen(
        template = template,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun EditMemeScreen(
    state: EditMemeState,
    template: MemeTemplate,
    onAction: (EditMemeAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    BackHandler(
        enabled = !state.isLeavingWithoutSaving && !state.isFinalisingMeme,
        onBack = { onAction(EditMemeAction.OnGoBackClick) }
    )

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onAction(EditMemeAction.ClearSelectedMemeText)
                }
            )
        },
        topBar = {
            TopBar(onGoBackClick = { onAction(EditMemeAction.OnGoBackClick) })
        },
        bottomBar = {
            BottomBar(
                onSaveMemeClick = { onAction(EditMemeAction.OnCompleteEditingClick) },
                onAddTextClick = { onAction(EditMemeAction.OnAddNewMemeTextClick) },
                onCancelFontResize = { onAction(EditMemeAction.OnCancelFontResize) },
                onConfirmFontResize = { onAction(EditMemeAction.OnConfirmMemeFontTextResize)},
                targetedMemeText = state.memeTexts.firstOrNull { it.id == state.textBoxInteraction.targetedTextBoxId },
                onFontValueChange = { id, newFontSize ->
                    onAction(
                        EditMemeAction.OnMemeTextFontSizeChange(
                            id = id,
                            fontSize = newFontSize
                        )
                    )
                }
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
                    textBoxInteractionState = state.textBoxInteraction,
                    onAction = onAction,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }

    // Conditional UI
    if (state.isFinalisingMeme) {
        SaveMemeContextSheetRoot(
            availableActions = listOf(
                MemeUiAction.Save(
                    onClick = {
                        onAction(EditMemeAction.OnSaveMemeClick(memeTemplate = template))
                    }
                ),
                MemeUiAction.Share(
                    onClick = {
                        onAction(EditMemeAction.OnShareMemeClick(memeTemplate = template))
                    }
                )
            ),
            onDismiss = {
                onAction(EditMemeAction.OnContinueEditing)
            },
            sheetState = sheetState,
        )
    }

    if (state.isLeavingWithoutSaving) {
        LeaveEditorConfirmationDialog(
            onDismiss = { onAction(EditMemeAction.OnCancelLeaveWithoutSaving) },
            onConfirmLeave = { onAction(EditMemeAction.OnConfirmLeaveWithoutSaving) }
        )
    }
}

@Composable
private fun DraggableContainer(
    modifier: Modifier = Modifier,
    children: List<MemeText>,
    textBoxInteractionState: TextBoxInteractionState,
    onAction: (EditMemeAction) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight

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
                    .pointerInput(child.id, textBoxInteractionState.targetedTextBoxId) {
                        detectDragGestures(
                            onDragEnd = {
                                // Update final position when drag ends
                                onAction(
                                    EditMemeAction.OnMemeTextPositionChange(
                                        id = child.id,
                                        x = offsetX,
                                        y = offsetY
                                    )
                                )
                            }
                        ) { change, dragAmount ->
                            if (textBoxInteractionState.targetedTextBoxId == child.id) {
                                change.consume()
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
                val isSelected = textBoxInteractionState is TextBoxInteractionState.Selected
                        && textBoxInteractionState.textBoxId == child.id
                val isEditing = textBoxInteractionState is TextBoxInteractionState.Editing
                        && textBoxInteractionState.textBoxId == child.id
                MemeTextBox(
                    memeText = child,
                    modifier = Modifier,
                    maxWidth = (parentWidth * 0.3f).dp,
                    maxHeight = (parentHeight * 0.3f).dp,
                    isSelected = isSelected,
                    isEditing = isEditing,
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
    targetedMemeText: MemeText?,
    onFontValueChange: (id: Int, fontSize: Float) -> Unit,
    onAddTextClick: () -> Unit,
    onSaveMemeClick: () -> Unit,
    onCancelFontResize: () -> Unit,
    onConfirmFontResize: () -> Unit,
) {
    if (targetedMemeText == null) {
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
    } else {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { onCancelFontResize() },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
                enabled = true,
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
            FontSlider(
                value = targetedMemeText.fontSize,
                onValueChange = { newFontSize ->
                    onFontValueChange(targetedMemeText.id, newFontSize)
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onConfirmFontResize() },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
                enabled = true,
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        EditMemeScreen(
            template = MemeTemplate.TEMPLATE_04,
            onAction = {},
            state = EditMemeState(
                textBoxInteraction = TextBoxInteractionState.Selected(0),
                memeTexts = listOf(
                    MemeText(
                        id = 0,
                        text = "Text #1",
                        offset = Offset(100f, 200f)
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