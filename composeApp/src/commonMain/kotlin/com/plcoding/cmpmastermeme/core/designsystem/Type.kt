package com.plcoding.cmpmastermeme.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.impact
import cmpmastermeme.composeapp.generated.resources.manrope
import org.jetbrains.compose.resources.Font

val Manrope @Composable get() = FontFamily(
    Font(
        resource = Res.font.manrope,
        weight = FontWeight.Normal
    ),
)

val Impact @Composable get() = FontFamily(
    Font(
        resource = Res.font.impact,
        weight = FontWeight.Bold
    )
)

val Typography.button: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

val Typography: Typography
    @Composable get() = Typography(
        displayLarge = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            lineHeight = 28.sp
        ),
        displayMedium = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        ),
    )