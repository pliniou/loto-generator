package com.cebolao.app.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cebolao.app.theme.CebolaoLayout
import com.cebolao.app.theme.LocalSpacing

@Composable
fun CebolaoContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .widthIn(max = CebolaoLayout.contentMaxWidth)
                    .padding(horizontal = spacing.lg),
        ) {
            content()
        }
    }
}
