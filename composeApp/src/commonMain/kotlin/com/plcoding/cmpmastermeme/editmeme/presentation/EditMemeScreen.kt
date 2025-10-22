@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.plcoding.cmpmastermeme.editmeme.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.add_text
import cmpmastermeme.composeapp.generated.resources.save_meme
import cmpmastermeme.composeapp.generated.resources.title_new_meme
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate
import com.plcoding.cmpmastermeme.core.presentation.ObserveAsEvents
import com.plcoding.cmpmastermeme.core.presentation.asString
import com.plcoding.cmpmastermeme.editmeme.presentation.EditMemeViewModel
import com.plcoding.cmpmastermeme.editmeme.presentation.components.MemePrimaryButton
import com.plcoding.cmpmastermeme.editmeme.presentation.components.MemeSecondaryButton
import com.plcoding.cmpmastermeme.editmeme.presentation.components.MemeTextBox
import com.plcoding.cmpmastermeme.editmeme.presentation.components.confirmationdialog.LeaveEditorConfirmationDialog
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeEvent
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeText
import com.plcoding.cmpmastermeme.editmeme.presentation.models.TextBoxInteractionState
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
    BackHandler(
        enabled = !state.isLeavingWithoutSaving,
        onBack = { onAction(EditMemeAction.OnGoBackClick) }
    )

    val windowSize = currentWindowDpSize()

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onAction(EditMemeAction.ClearSelectedMemeText)
                }
            )
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier.padding(vertical = 8.dp),
                onSaveMemeClick = { onAction(EditMemeAction.OnSaveMemeClick(memeTemplate = template)) },
                onAddTextClick = { onAction(EditMemeAction.OnAddNewMemeTextClick) },
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
                        .then(
                            if(windowSize.width > windowSize.height) {
                                Modifier.fillMaxHeight()
                            } else Modifier.fillMaxWidth()
                        )
                        .onSizeChanged { size ->
                            onAction(
                                EditMemeAction.OnContainerSizeChanged(size)
                            )
                        },
                    contentScale = if (windowSize.width > windowSize.height) {
                        ContentScale.FillHeight
                    } else {
                        ContentScale.FillWidth
                    }
                )
                DraggableContainer(
                    children = state.memeTexts,
                    textBoxInteractionState = state.textBoxInteraction,
                    onAction = onAction,
                    modifier = Modifier.matchParentSize()
                )
            }

            IconButton(
                onClick = {
                    onAction(EditMemeAction.OnGoBackClick)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
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
            var childWidth by remember(child.id) { mutableStateOf(0) }
            var childHeight by remember(child.id) { mutableStateOf(0) }

            // Without passing the key, the remembered state is remembered based on the position
            // of the Composable in this forEach UI tree.
            // If two texts are shown and the first is deleted, the new list size will be 1.
            // Compose will then use the old cached transform values from text 1 for text 2.
            var zoom by remember(child.id) {
                mutableStateOf(1f)
            }
            var offset by remember(child.id) {
                mutableStateOf(Offset(
                    x = child.offsetRatioX * parentWidth,
                    y = child.offsetRatioY * parentHeight
                ))
            }
            var rotation by remember(child.id) {
                mutableStateOf(0f)
            }

            val gestureState = rememberTransformableState { zoomChange, panChange, rotationChange ->
                // 1) Update rotation
                rotation += rotationChange

                // 2) Rotate pan change to account for rotation
                val angle = rotation * kotlin.math.PI.toFloat() / 180f
                val cos = kotlin.math.cos(angle)
                val sin = kotlin.math.sin(angle)

                val rotatedPanX = panChange.x * cos - panChange.y * sin
                val rotatedPanY = panChange.x * sin + panChange.y * cos

                // 3) Update zoom
                zoom = (zoom * zoomChange).coerceIn(0.5f, 5f)

                // 4) Calculate the axis-aligned bounding box of the rotated element
                val scaledWidth = childWidth * zoom
                val scaledHeight = childHeight * zoom

                // Visual bounds after rotation (absolute values since rotation can be any angle)
                val visualWidth =
                    kotlin.math.abs(scaledWidth * cos) + kotlin.math.abs(scaledHeight * sin)
                val visualHeight =
                    kotlin.math.abs(scaledWidth * sin) + kotlin.math.abs(scaledHeight * cos)

                // Offset from layout center to visual center due to scaling
                val scaleOffsetX = (scaledWidth - childWidth) / 2
                val scaleOffsetY = (scaledHeight - childHeight) / 2

                // Additional offset due to rotation changing the bounding box
                val rotationOffsetX = (visualWidth - scaledWidth) / 2
                val rotationOffsetY = (visualHeight - scaledHeight) / 2

                // Total visual extent
                val minX = scaleOffsetX + rotationOffsetX
                val maxX = parentWidth - childWidth - scaleOffsetX - rotationOffsetX
                val minY = scaleOffsetY + rotationOffsetY
                val maxY = parentHeight - childHeight - scaleOffsetY - rotationOffsetY

                offset = Offset(
                    x = (offset.x + zoom * rotatedPanX).coerceIn(
                        minOf(minX, maxX),
                        maxOf(minX, maxX)
                    ),
                    y = (offset.y + zoom * rotatedPanY).coerceIn(
                        minOf(minY, maxY),
                        maxOf(minY, maxY)
                    )
                )

                onAction(
                    EditMemeAction.OnMemeTextTransformChanged(
                        id = child.id,
                        offset = offset,
                        rotation = rotation,
                        scale = zoom
                    )
                )
            }

            Box(
                modifier = Modifier
                    .onSizeChanged {
                        childWidth = it.width
                        childHeight = it.height
                    }
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                        rotationZ = rotation
                        scaleX = zoom
                        scaleY = zoom
                    }
                    .transformable(gestureState)
            ) {
                val isSelected = textBoxInteractionState is TextBoxInteractionState.Selected
                        && textBoxInteractionState.textBoxId == child.id
                val isEditing = textBoxInteractionState is TextBoxInteractionState.Editing
                        && textBoxInteractionState.textBoxId == child.id

                MemeTextBox(
                    memeText = child,
                    modifier = Modifier,
                    maxWidth = (parentWidth * 0.3f / zoom).dp,
                    maxHeight = (parentHeight * 0.3f / zoom).dp,
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
                    onDelete = {
                        onAction(
                            EditMemeAction.OnDeleteMemeText(
                                child.id
                            )
                        )
                    },
                    onClick = {
                        onAction(
                            EditMemeAction.OnSelectMemeText(
                                child.id
                            )
                        )
                    },
                    onDoubleClick = {
                        onAction(
                            EditMemeAction.OnEditMemeText(
                                child.id
                            )
                        )
                    }
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
    onSaveMemeClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
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
            template = MemeTemplate.TEMPLATE_04,
            onAction = {},
            state = EditMemeState(
                textBoxInteraction = TextBoxInteractionState.Selected("0"),
                memeTexts = listOf(
                    MemeText(
                        id = "0",
                        text = "Text #1",
                        offsetRatioX = 0f,
                        offsetRatioY = 0f
                    ),
                    MemeText(
                        id = "1",
                        text = "Text #2",
                    )
                )
            )
        )
    }
}