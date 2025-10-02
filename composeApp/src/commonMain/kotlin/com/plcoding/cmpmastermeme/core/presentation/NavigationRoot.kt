package com.plcoding.cmpmastermeme.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plcoding.cmpmastermeme.editmeme.EditMemeScreenRoot
import com.plcoding.cmpmastermeme.memelist.MemeListScreenRoot
import kotlinx.serialization.Serializable

sealed interface MemeMasterGraph {
    @Serializable
    data object MemeList : MemeMasterGraph

    @Serializable
    data object EditMeme : MemeMasterGraph
}

@Composable
fun NavigationRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = MemeMasterGraph.MemeList,
    ) {
        composable<MemeMasterGraph.MemeList> {
            MemeListScreenRoot(
                onMemeTemplateSelected = {
                    navController.navigate(MemeMasterGraph.EditMeme)
                }
            )
        }
        composable<MemeMasterGraph.EditMeme> {
            EditMemeScreenRoot(
                onGoBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}