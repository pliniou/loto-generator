package com.cebolao.app.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.ErrorState
import com.cebolao.app.component.LoadingState
import com.cebolao.app.core.UiEvent
import com.cebolao.app.feature.home.components.LargeScreenHomeContent
import com.cebolao.app.feature.home.components.RefreshButton
import com.cebolao.app.feature.home.components.SmallScreenHomeContent
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.util.toUserMessage
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isLargeScreen: Boolean = false,
    onNavigateToChecker: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    val hasAnyContest = uiState.contests.values.any { it != null }

    // Collect one-shot events for Snackbar
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            com.cebolao.app.component.CebolaoTopAppBar(
                title = stringResource(R.string.app_name),
                actions = {
                    RefreshButton(
                        onRefresh = { viewModel.refreshData() },
                        isRefreshing = uiState.isSyncing,
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = spacing.lg),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (uiState.isLoading && !hasAnyContest) {
                LoadingState(modifier = Modifier.fillMaxSize())
            } else if (uiState.error != null && !hasAnyContest) {
                ErrorState(
                    message = uiState.error?.toUserMessage() ?: stringResource(R.string.state_error),
                    onRetry = { viewModel.refreshData() },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                if (isLargeScreen) {
                    // Two-pane layout for large screens
                    LargeScreenHomeContent(
                        uiState = uiState,
                        spacing = spacing,
                        onNavigateToChecker = onNavigateToChecker,
                        onToggleUpcomingDraws = { viewModel.toggleUpcomingDrawsExpanded() },
                        onToggleLatestResults = { viewModel.toggleLatestResultsExpanded() },
                    )
                } else {
                    // Single column layout for small screens
                    SmallScreenHomeContent(
                        uiState = uiState,
                        spacing = spacing,
                        onNavigateToChecker = onNavigateToChecker,
                        onToggleUpcomingDraws = { viewModel.toggleUpcomingDrawsExpanded() },
                        onToggleLatestResults = { viewModel.toggleLatestResultsExpanded() },
                    )
                }
            }
        }
    }
}
