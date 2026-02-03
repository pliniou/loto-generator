package com.cebolao.app.feature.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.component.ConfirmationDialog
import com.cebolao.app.component.TeamSelectionDialog
import com.cebolao.app.feature.generator.components.GeneratedGameItem
import com.cebolao.app.component.DrawingAnimation
import com.cebolao.app.component.GameDetailsDialog
import com.cebolao.app.feature.generator.components.GeneratorConfigSection
import com.cebolao.app.feature.generator.components.GeneratorFilterConfigDialog
import com.cebolao.app.feature.generator.components.GeneratorReportDetailsDialog
import com.cebolao.app.feature.generator.components.GeneratorResultsSection
import com.cebolao.app.feature.generator.components.TimemaniaTeamCard
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(viewModel: GeneratorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val haptic = LocalHapticFeedback.current

    var showTeamDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var selectedGameForDetails by remember { mutableStateOf<com.cebolao.domain.model.Game?>(null) }

    // Dialogs
    if (showClearConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_clear_title),
            text = stringResource(R.string.dialog_clear_message),
            confirmText = stringResource(R.string.action_clear),
            onConfirm = {
                viewModel.onClearGenerated()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false },
        )
    }

    if (showTeamDialog) {
        TeamSelectionDialog(
            onDismissRequest = { showTeamDialog = false },
            onTeamSelected = { viewModel.onTeamSelected(it) },
        )
    }

    if (uiState.showFilterConfigDialog) {
        GeneratorFilterConfigDialog(
            uiState = uiState,
            onClose = { viewModel.onCloseFilterConfig() },
            onApplyPreset = { viewModel.onApplyPresetForProfile() },
            onUpdateFilterConfig = { f, cfg -> viewModel.onUpdateFilterConfig(f, cfg) },
            onToggleFilter = { viewModel.onFilterToggled(it) },
        )
    }

    if (uiState.showReportDialog && uiState.generationReport != null) {
        GeneratorReportDetailsDialog(
            uiState = uiState,
            onClose = { viewModel.onCloseReportDetails() },
        )
    }

    // Game details dialog
    selectedGameForDetails?.let { game ->
        GameDetailsDialog(
            game = game,
            lastContest = uiState.lastContest,
            onClose = { selectedGameForDetails = null },
        )
    }

    // Feedback de salvamento e háptica
    LaunchedEffect(uiState.lastSavedCount) {
        if (uiState.lastSavedCount > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val message =
                context.resources.getQuantityString(
                    R.plurals.snackbar_games_saved,
                    uiState.lastSavedCount,
                    uiState.lastSavedCount,
                )
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    CebolaoContent {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = spacing.lg, bottom = 120.dp), // Espaço para a barra inferior
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                // 1. Config Section
                item {
                    GeneratorConfigSection(
                        uiState = uiState,
                        onTypeSelected = { viewModel.onTypeSelected(it) },
                        onQuantityChanged = { viewModel.onQuantityChanged(it) },
                        onFilterToggled = { viewModel.onFilterToggled(it) },
                        onOpenFilterConfig = { viewModel.onOpenFilterConfig() },
                    )
                }

                // 2. Timemania
                item {
                    TimemaniaTeamCard(
                        uiState = uiState,
                        onShowTeamDialog = { showTeamDialog = true },
                    )
                }

                // 3. Ação Gerar
                if (uiState.generatedGames.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Button(
                            onClick = { viewModel.onGenerate() },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                            shape = MaterialTheme.shapes.large,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = LotteryColors.getColor(uiState.selectedType),
                                    contentColor = LotteryColors.getOnColor(uiState.selectedType),
                                ),
                        ) {
                            Text(
                                text =
                                    pluralStringResource(
                                        R.plurals.action_generate,
                                        uiState.quantity,
                                        uiState.quantity,
                                    ),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                // 4. Resultados
                if (uiState.generatedGames.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = spacing.sm),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
                        )
                        GeneratorResultsSection(
                            uiState = uiState,
                            onOpenReportDetails = { viewModel.onOpenReportDetails() },
                            onRetry = { viewModel.onGenerate() },
                        )
                    }

                    items(items = uiState.generatedGames, key = { it.id }) { game ->
                        GeneratedGameItem(
                            game = game,
                            lastContest = uiState.lastContest,
                            onClick = { selectedGameForDetails = game },
                        )
                    }
                }
            }

            // Overlay de Carregamento (Drawing Animation)
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = AlphaLevels.OVERLAY_DARK)),
                    contentAlignment = Alignment.Center,
                ) {
                    DrawingAnimation(color = LotteryColors.getColor(uiState.selectedType))
                }
            }

            // Barra de Ações Inferior Redesenhada (Horizontal e Compacta)
            AnimatedVisibility(
                visible = uiState.generatedGames.isNotEmpty() && !uiState.isLoading,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = AlphaLevels.GLASS_HIGH),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedButton(
                            onClick = { showClearConfirmation = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            border =
                                androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = AlphaLevels.BORDER_MEDIUM),
                                ),
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Text(stringResource(R.string.action_cancel), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.onSaveAll() },
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Text(stringResource(R.string.action_save), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (uiState.generatedGames.isNotEmpty()) 100.dp else spacing.lg),
            )
        }
    }
}


