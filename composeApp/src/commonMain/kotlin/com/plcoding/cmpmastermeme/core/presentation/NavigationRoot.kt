package com.plcoding.cmpmastermeme.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.editmeme.EditMemeScreenRoot
import com.plcoding.cmpmastermeme.memelist.MemeListScreenRoot
import kotlinx.serialization.Serializable

sealed interface MemeMasterGraph {
    @Serializable
    data object MemeList : MemeMasterGraph

    @Serializable
    data class EditMeme(val templateId: String) : MemeMasterGraph
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
                onNavigateToEditTemplateSelected = { template ->
                    navController.navigate(MemeMasterGraph.EditMeme(template.id))
                }
            )
        }
        composable<MemeMasterGraph.EditMeme> { backStackEntry ->
            val templateId = backStackEntry.toRoute<MemeMasterGraph.EditMeme>().templateId
            // NOTE can also provide default template if app should not fail fast: ?: MemeTemplate.TEMPLATE_01
            val template = MemeTemplate.fromId(templateId) ?: run {
                navController.navigateUp()
                null
            }
            template?.let {
                EditMemeScreenRoot(
                    template = it,
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}