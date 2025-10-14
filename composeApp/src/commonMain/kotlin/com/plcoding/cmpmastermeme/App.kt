package com.plcoding.cmpmastermeme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.domain.LocalMemeDataSource
import com.plcoding.cmpmastermeme.core.domain.Meme
import com.plcoding.cmpmastermeme.core.presentation.NavigationRoot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val ds = koinInject<LocalMemeDataSource>()
    val navController = rememberNavController()
    MasterMemeTheme {
        NavigationRoot(navController)
    }
    LaunchedEffect(Unit) {
        ds.save(
            Meme(
                imageUri = "TEST URI PATH",
            )
        )
        delay(2000)
        ds.observeAll().onEach {
            it[0].apply {
                println("GIMME: $this")
            }
        }.collect()
    }
}