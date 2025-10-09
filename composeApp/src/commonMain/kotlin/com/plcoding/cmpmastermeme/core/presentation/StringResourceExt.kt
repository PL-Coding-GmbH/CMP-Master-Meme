package com.plcoding.cmpmastermeme.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * More Kotlin-idiomatic style of writing
 */
@Composable
fun StringResource.asString(): String {
    return stringResource(this)
}