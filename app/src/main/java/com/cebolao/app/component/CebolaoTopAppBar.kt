package com.cebolao.app.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CebolaoTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.content_description_back),
                    )
                }
            }
        },
        actions = { actions() },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = AlphaLevels.GLASS_HIGH),
                scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = AlphaLevels.GLASS_HIGH),
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
    )
}
