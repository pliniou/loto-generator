package com.cebolao.app.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.util.StatisticsUtil
import com.cebolao.domain.util.TimemaniaUtil
import java.util.Locale

/**
 * Dialog que mostra estatísticas detalhadas de um jogo gerado.
 */
@Composable
fun GameDetailsDialog(
    game: Game,
    lastContest: Contest?,
    onClose: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(game.lotteryType)

    // Calculate full insights
    val insight =
        remember(game, lastContest) {
            StatisticsUtil.analyzeGame(game.numbers, lastContest, emptyList())
        }

    // Calculate decade distribution
    val decadeDistribution =
        remember(game) {
            StatisticsUtil.calculateDecadeDistribution(game.numbers)
        }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(stringResource(R.string.action_close))
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "Estatísticas do Jogo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = lotteryColor,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // Numbers display
                Text(
                    text = "Números",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    game.numbers.sorted().take(7).forEach { number ->
                        SmallLotteryBall(
                            number = number,
                            lotteryType = game.lotteryType,
                        )
                    }
                }
                if (game.numbers.size > 7) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        game.numbers.sorted().drop(7).forEach { number ->
                            SmallLotteryBall(
                                number = number,
                                lotteryType = game.lotteryType,
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))

                // Basic Stats Section
                Text(
                    text = "Estatísticas Básicas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                StatsGrid(
                    items =
                        listOf(
                            "Soma" to "${insight.sum}",
                            "Média" to String.format(Locale.getDefault(), "%.1f", insight.average),
                            "Pares" to "${insight.evenCount}",
                            "Ímpares" to "${insight.oddCount}",
                        ),
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))

                // Advanced Stats Section
                Text(
                    text = "Estatísticas Avançadas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                StatsGrid(
                    items =
                        listOf(
                            "Primos" to "${insight.primeCount}",
                            "Múlt. 3" to "${insight.multiplesOf3}",
                            "Sequência" to "${insight.longestSequence}",
                            "Repetidos" to "${insight.repeatsFromLast}",
                        ),
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))

                // Decade Distribution
                Text(
                    text = "Distribuição por Dezena",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    val maxDecadeCount = decadeDistribution.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                    decadeDistribution.toSortedMap().forEach { (decade, count) ->
                        val startNum = decade * 10
                        val endNum = startNum + 9
                        val fraction = count.toFloat() / maxDecadeCount.toFloat()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Text(
                                text = "${String.format(
                                    Locale.getDefault(),
                                    "%02d",
                                    if (startNum == 0) 1 else startNum,
                                )}-${String.format(Locale.getDefault(), "%02d", endNum)}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(48.dp),
                            )
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { fraction },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(8.dp),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                color = lotteryColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(24.dp),
                            )
                        }
                    }
                }

                // Team info for Timemania
                if (game.teamNumber != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
                    Text(
                        text = "Time do Coração",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = TimemaniaUtil.getTeamName(game.teamNumber),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = lotteryColor,
                    )
                }

                // Last contest comparison
                if (lastContest != null && insight.repeatsFromLast > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT))
                    Text(
                        text = "Comparação com Último Sorteio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    val matches = game.numbers.intersect(lastContest.getAllNumbers().toSet())
                    Text(
                        text = "Números coincidentes: ${matches.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                    )
                }
            }
        },
    )
}

@Composable
private fun StatsGrid(items: List<Pair<String, String>>) {
    val spacing = LocalSpacing.current

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { (label, value) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                        )
                    }
                }
            }
        }
    }
}
