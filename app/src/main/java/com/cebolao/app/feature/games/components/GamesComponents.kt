package com.cebolao.app.feature.games.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.SmallLotteryBall
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.Game
import com.cebolao.domain.util.TimemaniaUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedGameItem(
    game: Game,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit = {},
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(game.lotteryType)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    // Calculate insights
    val insight =
        remember(game) {
            com.cebolao.domain.util.StatisticsUtil.analyzeGame(game.numbers, null, emptyList())
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, lotteryColor.copy(alpha = 0.15f)),
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
                        modifier = Modifier.size(40.dp),
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
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                },
                            modifier = Modifier.size(20.dp),
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
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_remove),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    )
                    SavedInsightBadge(
                        label = "${insight.evenCount}P / ${insight.oddCount}I",
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    )
                    SavedInsightBadge(
                        label = "Média: ${insight.average}",
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    )
                }

                // Segunda linha de insights detalhados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    SavedInsightBadge(
                        label = "Seq: ${insight.longestSequence}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    )
                    SavedInsightBadge(
                        label = "Múlt3: ${insight.multiplesOf3}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    )
                    SavedInsightBadge(
                        label = "Primos: ${insight.primeCount}",
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    )
                }

                Text(
                    text = dateFormat.format(Date(game.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
