package com.cebolao.app.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cebolao.R
import com.cebolao.app.component.WelcomeBanner
import com.cebolao.app.theme.CebolaoSpacing
import com.cebolao.domain.model.LotteryType
import com.cebolao.app.feature.home.HomeUiState
import kotlinx.coroutines.launch

@Composable
fun SmallScreenHomeContent(
    uiState: HomeUiState,
    spacing: CebolaoSpacing,
    onNavigateToChecker: () -> Unit,
    onToggleUpcomingDraws: () -> Unit,
    onToggleLatestResults: () -> Unit,
) {
    val listState = rememberLazyListState()
    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 || listState.firstVisibleItemScrollOffset > 200 }
    }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = spacing.lg,
                top = spacing.lg,
                end = spacing.lg,
                bottom = spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
            modifier = Modifier.fillMaxSize(),
        ) {
            item { WelcomeBanner() }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_schedule_title),
                    actionText = if (!uiState.upcomingDrawsExpanded && LotteryType.entries.size > 3) "Ver Todos" else null,
                    onActionClick = onToggleUpcomingDraws,
                )
            }

            item {
                UpcomingDrawsSection(
                    contests = uiState.contests,
                    isExpanded = uiState.upcomingDrawsExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded && !uiState.upcomingDrawsExpanded) {
                            onToggleUpcomingDraws()
                        }
                    },
                )
            }

            item { SectionDivider() }

            item {
                SectionHeader(
                    title = "Últimos Resultados",
                    actionText = if (!uiState.latestResultsExpanded && LotteryType.entries.size > 3) "Ver Todos" else null,
                    onActionClick = onToggleLatestResults,
                )
            }

            item {
                LatestResultsSection(
                    contests = uiState.contests,
                    onNavigateToChecker = onNavigateToChecker,
                    isExpanded = uiState.latestResultsExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded && !uiState.latestResultsExpanded) {
                            onToggleLatestResults()
                        }
                    },
                )
            }
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = showBackToTop,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(150)),
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(end = spacing.lg, bottom = spacing.lg),
        ) {
            FloatingActionButton(
                onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Voltar ao topo")
            }
        }
    }
}

@Composable
fun LargeScreenHomeContent(
    uiState: HomeUiState,
    spacing: CebolaoSpacing,
    onNavigateToChecker: () -> Unit,
    onToggleUpcomingDraws: () -> Unit,
    onToggleLatestResults: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        WelcomeBanner()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                SectionHeader(
                    title = stringResource(R.string.home_schedule_title),
                    actionText = if (!uiState.upcomingDrawsExpanded && LotteryType.entries.size > 3) "Ver Todos" else null,
                    onActionClick = onToggleUpcomingDraws,
                )

                UpcomingDrawsSection(
                    contests = uiState.contests,
                    isExpanded = uiState.upcomingDrawsExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded && !uiState.upcomingDrawsExpanded) {
                            onToggleUpcomingDraws()
                        }
                    },
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                SectionHeader(
                    title = "Últimos Resultados",
                    actionText = if (!uiState.latestResultsExpanded && LotteryType.entries.size > 3) "Ver Todos" else null,
                    onActionClick = onToggleLatestResults,
                )

                LatestResultsSection(
                    contests = uiState.contests,
                    onNavigateToChecker = onNavigateToChecker,
                    isExpanded = uiState.latestResultsExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded && !uiState.latestResultsExpanded) {
                            onToggleLatestResults()
                        }
                    },
                )
            }
        }
    }
}
