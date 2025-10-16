package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.delete_meme
import cmpmastermeme.composeapp.generated.resources.delete_meme_desc
import cmpmastermeme.composeapp.generated.resources.icon_save
import cmpmastermeme.composeapp.generated.resources.save_to_device
import cmpmastermeme.composeapp.generated.resources.save_to_device_desc
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
    data class Save(override val onClick: () -> Unit) : MemeUiAction(
        titleRes = Res.string.save_to_device,
        descriptionRes = Res.string.save_to_device_desc,
        vectorRes = Res.drawable.icon_save,
        onClick = onClick
    )

    data class Share(override val onClick: () -> Unit) : MemeUiAction(
        titleRes = Res.string.share_meme,
        descriptionRes = Res.string.share_meme_desc,
        icon = Icons.Outlined.Share,
        onClick = onClick
    )

    data class Delete(override val onClick: () -> Unit) : MemeUiAction(
        titleRes = Res.string.delete_meme,
        descriptionRes = Res.string.delete_meme_desc,
        icon = Icons.Outlined.Delete,
        onClick = onClick
    )
}