package com.cebolao.app.feature.games.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.SmallLotteryBall
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.CebolaoElevation
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.Game
import com.cebolao.domain.util.TimemaniaUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Shared date formatter to avoid expensive object creation on every recomposition
private val savedGameDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

@Composable
fun SavedGameItem(
    game: Game,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit = {},
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(game.lotteryType)

    // Calculate insights (already optimized with remember(game))
    val insight = remember(game) { com.cebolao.domain.util.StatisticsUtil.analyzeGame(game.numbers, null, emptyList()) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = CebolaoElevation.level2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(ComponentDimensions.strokeWidthMedium, lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT)),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            // Cabeçalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    // Pin/Favorite indicator
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .size(ComponentDimensions.buttonHeightSmall),
                    ) {
                        Icon(
                            imageVector = if (game.isPinned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription =
                                stringResource(
                                    if (game.isPinned) {
                                        R.string.action_unpin
                                    } else {
                                        R.string.action_pin
                                    },
                                ),
                            tint =
                                if (game.isPinned) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW)
                                },
                            modifier = Modifier.size(ComponentDimensions.iconSizeSmall),
                        )
                    }

                    Text(
                        text = stringResource(LotteryUiMapper.getNameRes(game.lotteryType)),
                        style = MaterialTheme.typography.titleMedium,
                        color = lotteryColor,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .size(ComponentDimensions.iconSizeMedium),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_remove),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // Exibir Time (Timemania)
            if (game.teamNumber != null) {
                Text(
                    text = TimemaniaUtil.getTeamName(game.teamNumber),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )
            }

            // Bolinhas
            val chunks = game.numbers.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                chunks.forEach { chunk ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        chunk.forEach { number ->
                            SmallLotteryBall(number = number, lotteryType = game.lotteryType)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))
            HorizontalDivider(thickness = ComponentDimensions.dividerThicknessThin, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
            Spacer(modifier = Modifier.height(spacing.md))

            // Rodapé com Insights e Data
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                // Primeira linha de insights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    SavedInsightBadge(
                        label = stringResource(R.string.insight_sum, insight.sum),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                    SavedInsightBadge(
                        label = "${insight.evenCount}P / ${insight.oddCount}I",
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                    SavedInsightBadge(
                        label = "Média: ${insight.average}",
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                    )
                }

                // Segunda linha de insights detalhados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    SavedInsightBadge(
                        label = "Seq: ${insight.longestSequence}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                    SavedInsightBadge(
                        label = "Múlt3: ${insight.multiplesOf3}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                    SavedInsightBadge(
                        label = "Primos: ${insight.primeCount}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                    )
                }

                // Taxa de acertos (novo)
                if (game.recentHitRate > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Taxa de acertos em 10 concursos: ${(game.recentHitRate * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = { game.recentHitRate },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(MaterialTheme.shapes.small),
                            color = lotteryColor,
                            trackColor = lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT),
                        )
                    }
                }

                Text(
                    text = savedGameDateFormat.format(Date(game.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SavedInsightBadge(
    label: String,
    color: androidx.compose.ui.graphics.Color,
) {
    val spacing = LocalSpacing.current
    Surface(
        color = color,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
