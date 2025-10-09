package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.button
import com.plcoding.cmpmastermeme.core.designsystem.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MemeSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = ButtonDefaults.outlinedButtonBorder().copy(
            width = 1.dp,
            brush = MaterialTheme.colorScheme.extended.buttonGradient
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button.copy(
                brush = MaterialTheme.colorScheme.extended.buttonGradient
            ),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        MemeSecondaryButton(
            text = "Edit meme",
            onClick = {}
        )
    }
}