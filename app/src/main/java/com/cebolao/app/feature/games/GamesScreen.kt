package com.cebolao.app.feature.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.feature.games.components.SavedGameItem
import com.cebolao.app.component.GameDetailsDialog
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel(),
    onNavigateToGenerator: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedGameForDetails by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    var gameToDelete by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    if (gameToDelete != null) {
        com.cebolao.app.component.ConfirmationDialog(
            title = stringResource(R.string.dialog_delete_title),
            text = stringResource(R.string.dialog_delete_message),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                gameToDelete?.let { viewModel.onDeleteGame(it) }
            },
            onDismiss = { gameToDelete = null },
        )
    }

    // Game details dialog
    selectedGameForDetails?.let { game ->
        GameDetailsDialog(
            game = game,
            lastContest = null, // In Saved Games we don't necessarily have the last contest context
            onClose = { selectedGameForDetails = null },
        )
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    CebolaoContent {
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = spacing.lg),
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Cabeçalho da Lista
                Text(
                    text = stringResource(R.string.games_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )

                // Filtros (Chips Neon)
                LazyRow(
                    contentPadding = PaddingValues(vertical = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    item {
                        val isSelected = uiState.filterType == null
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onFilterChanged(null) },
                            label = { Text(stringResource(R.string.filter_all)) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                        )
                    }
                    items(LotteryType.entries) { type ->
                        val isSelected = type == uiState.filterType
                        val chipColor by animateColorAsState(
                            targetValue = LotteryColors.getColor(type),
                            animationSpec = tween(durationMillis = 300),
                            label = "chip-color-$type"
                        )
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.onFilterChanged(null)
                                } else {
                                    viewModel.onFilterChanged(type)
                                }
                            },
                            label = { Text(stringResource(LotteryUiMapper.getNameRes(type))) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor,
                                    selectedLabelColor = LotteryColors.getOnColor(type),
                                ),
                            border =
                                if (isSelected) {
                                    null
                                } else {
                                    FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = false,
                                        borderColor = chipColor.copy(alpha = AlphaLevels.BORDER_MEDIUM),
                                    )
                                },
                        )
                    }
                }

                if (uiState.games.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(spacing.xl),
                        ) {
                            androidx.compose.material3.Surface(
                                modifier = Modifier.size(80.dp),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.lg))

                            Text(
                                text = stringResource(R.string.state_empty_games),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.height(spacing.md))

                            Button(
                                onClick = onNavigateToGenerator,
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(text = stringResource(R.string.action_generate_now), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = spacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        items(uiState.games, key = { it.id }) { game ->
                            SavedGameItem(
                                game = game,
                                onDelete = { gameToDelete = game },
                                onTogglePin = { viewModel.onTogglePin(game) },
                                onClick = { selectedGameForDetails = game },
                            )
                        }
                    }
                }
            }
        }
    }
}


