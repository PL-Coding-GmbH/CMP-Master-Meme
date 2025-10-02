package com.plcoding.cmpmastermeme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.presentation.NavigationRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    MasterMemeTheme {
        NavigationRoot(navController)
    }
}