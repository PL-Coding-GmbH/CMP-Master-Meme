package com.plcoding.cmpmastermeme.editmeme.presentation.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.vector.ImageVector
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.share_meme
import cmpmastermeme.composeapp.generated.resources.share_meme_desc
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class MemeUiAction(
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val icon: ImageVector? = null,
    val vectorRes: DrawableResource? = null,
    open val onClick: () -> Unit,
) {
    data class Share(override val onClick: () -> Unit) : MemeUiAction(
        titleRes = Res.string.share_meme,
        descriptionRes = Res.string.share_meme_desc,
        icon = Icons.Outlined.Share,
        onClick = onClick
    )
}