@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MemeTemplateListContent(
    memeTemplates: List<MemeTemplate>,
    onMemeTemplateSelected: (MemeTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier,
    ) {
        itemsIndexed(
            items = memeTemplates,
            key = { index, template -> template.id }
        ) { index, template ->
            Card(
                onClick = { onMemeTemplateSelected(template) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(template.drawableResource),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        MemeTemplateListContent(
            memeTemplates = MemeTemplate.entries,
            onMemeTemplateSelected = {}
        )
    }
}