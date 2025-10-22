package com.plcoding.cmpmastermeme.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    surfaceContainerLowest = Color(0xFF0F0D13),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    outline = Color(0xFF79747E),
    primary = Color(0xFF65558F),
    secondary = Color(0xFFCCC2DC),
    onSurface = Color(0xFFE6E0E9),
    primaryContainer = Color(0xFFEADDFF),
    error = Color(0xFFB3261E),
    onPrimary = Color(0xFF21005D),
)