package com.cebolao.app.feature.checker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.R
import com.cebolao.app.feature.checker.components.AnalysisStatsSection
import com.cebolao.app.feature.checker.components.CheckerDuplaModeSelector
import com.cebolao.app.feature.checker.components.CheckerNumberGrid
import com.cebolao.app.feature.checker.components.CheckerResultCard
import com.cebolao.app.feature.checker.components.CheckerTypeSelector
import com.cebolao.app.feature.checker.components.HistoryHitsList
import com.cebolao.app.feature.checker.components.HistorySummaryRow
import com.cebolao.app.feature.checker.components.NumberStatsSection
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.domain.model.LotteryType
import com.cebolao.app.component.ScannerAnimation
import com.cebolao.domain.util.TimemaniaUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckerScreen(viewModel: CheckerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val haptic = LocalHapticFeedback.current
    val lotteryColor = LotteryColors.getColor(uiState.selectedType)

    var showTeamDialog by remember { mutableStateOf(false) }

    if (showTeamDialog) {
        com.cebolao.app.component.TeamSelectionDialog(
            onDismissRequest = { showTeamDialog = false },
            onTeamSelected = { viewModel.onTeamSelected(it) },
        )
    }

    // Integração de háptica
    LaunchedEffect(uiState.checkResult) {
        if (uiState.checkResult != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.isAnalyzing) {
        if (uiState.isAnalyzing) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Validação de seleção
    val isSelectionValid =
        if (uiState.selectedType == LotteryType.SUPER_SETE) {
            uiState.selectedNumbers.size == 7 && uiState.selectedNumbers.none { it == -1 }
        } else {
            val required = uiState.profile?.numbersPerGame
            if (required == null) {
                uiState.selectedNumbers.isNotEmpty()
            } else {
                uiState.selectedNumbers.size == required
            }
        }

    val hasResults = uiState.checkResult != null || uiState.analysisResults.isNotEmpty()

    CebolaoContent {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // Cabeçalho
                item {
                    Text(
                        text = stringResource(R.string.checker_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = spacing.sm),
                    )
                }

                // 1. Seletor de tipo (Chips Neon)
                item {
                    CheckerTypeSelector(
                        selectedType = uiState.selectedType,
                        onTypeSelected = { viewModel.onTypeSelected(it) },
                    )
                }

                // 1.5 Modo Dupla Sena
                if (uiState.selectedType == LotteryType.DUPLA_SENA) {
                    item {
                        CheckerDuplaModeSelector(
                            selectedMode = uiState.selectedDuplaMode,
                            onModeSelected = { viewModel.onDuplaModeSelected(it) },
                        )
                    }
                }

                // 2. Info do concurso atual
                item {
                    if (uiState.lastContest != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                            ),
                        ) {
                            Row(
                                modifier = Modifier.padding(spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    tint = lotteryColor,
                                    modifier = Modifier.size(24.dp),
                                )
                                Column {
                                    Text(
                                        text = "Conferir contra Concurso ${uiState.lastContest?.id ?: 0}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = uiState.lastContest?.drawDate ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Grade de números ou Super Sete
                item {
                    if (uiState.selectedType == LotteryType.SUPER_SETE) {
                        com.cebolao.app.component.SuperSeteInput(
                            selectedNumbers = uiState.selectedNumbers,
                            onNumberClick = { col, num ->
                                viewModel.onSuperSeteNumberSelected(col, num)
                            },
                        )
                    } else {
                        // Grid com altura fixa para garantir scroll
                        Box(modifier = Modifier.height(280.dp)) {
                            CheckerNumberGrid(
                                uiState = uiState,
                                onNumberToggle = { viewModel.onNumberToggle(it) },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }

                // 3.5 Time do Coração (Timemania)
                if (uiState.selectedType == LotteryType.TIMEMANIA) {
                    item {
                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .semantics { role = Role.Button }
                                    .clickable { showTeamDialog = true },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_LOW)),
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.lg),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        stringResource(R.string.timemania_team_heart),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = uiState.selectedTeam?.let { TimemaniaUtil.getTeamName(it) }
                                            ?: stringResource(R.string.checker_select_team),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = lotteryColor)
                            }
                        }
                    }
                }

                // Botão de Ação
                item {
                    Button(
                        onClick = {
                            viewModel.onCheck()
                            viewModel.onAnalyzeHistory()
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        enabled = isSelectionValid && uiState.lastContest != null && !uiState.isAnalyzing,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lotteryColor,
                            contentColor = LotteryColors.getOnColor(uiState.selectedType),
                        ),
                    ) {
                        Text(
                            text = if (uiState.isAnalyzing) "Analisando..." else stringResource(R.string.action_check) + " & " + stringResource(R.string.action_analyze),
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                // ============== SEÇÃO DE RESULTADOS ==============

                // Resultado principal do último concurso
                if (uiState.checkResult != null) {
                    item {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = "Resultado",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = lotteryColor,
                        )
                    }

                    item {
                        CheckerResultCard(uiState = uiState)
                    }
                }

                // Resumo de estatísticas
                if (hasResults && uiState.totalContestsChecked > 0) {
                    item {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = "Estatísticas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = lotteryColor,
                        )
                    }

                    item {
                        HistorySummaryRow(
                            total = uiState.totalContestsChecked,
                            best = uiState.bestHit,
                            prizeCount = uiState.prizeCount,
                            color = lotteryColor,
                        )
                    }
                }

                // Análise de acertos históricos (inline)
                if (uiState.analysisResults.isNotEmpty()) {
                    item {
                        AnalysisStatsSection(
                            results = uiState.analysisResults,
                            color = lotteryColor,
                        )
                    }
                }

                // Gráfico de distribuição de acertos
                if (uiState.historyResults.isNotEmpty()) {
                    item {
                        HistoryHitsList(history = uiState.historyResults, color = lotteryColor)
                    }
                }

                // Estatísticas de números (frequência e atraso)
                if (uiState.numberStats.isNotEmpty()) {
                    item {
                        NumberStatsSection(stats = uiState.numberStats, color = lotteryColor)
                    }
                }

                // Espaço final
                item {
                    Spacer(modifier = Modifier.height(spacing.xxl))
                }
            }

            // Scanner Overlay
            AnimatedVisibility(
                visible = uiState.isAnalyzing,
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
                    ScannerAnimation(color = lotteryColor)
                }
            }
        }
    }
}


