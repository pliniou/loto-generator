package com.cebolao.app.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType

@Composable
fun LotteryCard(
    contest: Contest?,
    lotteryType: LotteryType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(lotteryType)

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.sm),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp,
                hoveredElevation = 3.dp,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = lotteryColor.copy(alpha = 0.3f),
            ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(spacing.lg),
        ) {
            // Cabeçalho: nome da loteria + número do concurso
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(LotteryUiMapper.getNameRes(lotteryType)),
                    style = MaterialTheme.typography.titleLarge,
                    color = lotteryColor,
                    fontWeight = FontWeight.Bold,
                )

                if (contest != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.contest_short, contest.id),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (contest.accumulated) {
                            Text(
                                text = "ACUMULOU!",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.state_no_data),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            if (contest != null) {
                Spacer(modifier = Modifier.height(spacing.sm))

                // Draw Date
                Text(
                    text = contest.drawDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Visual Separator
                HorizontalDivider(
                    thickness = 1.dp,
                    color = lotteryColor.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Numbers Section
                BallsLayout(numbers = contest.numbers, type = lotteryType)

                // Second Draw for Dupla Sena
                if (lotteryType == LotteryType.DUPLA_SENA && contest.secondDrawNumbers != null) {
                    Spacer(modifier = Modifier.height(spacing.md))
                    Text(
                        text = stringResource(R.string.second_draw),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    BallsLayout(numbers = contest.secondDrawNumbers, type = lotteryType)
                }

                // Prize List Section
                if (!contest.prizeList.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(spacing.md))

                    // Visual Separator
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = lotteryColor.copy(alpha = 0.2f),
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    Text(
                        text = "Premiação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = lotteryColor,
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Show top prize tiers (limit to first 4 to avoid card being too large)
                    contest.prizeList.take(4).forEach { prize ->
                        PrizeTierRow(
                            prize = prize,
                            lotteryColor = lotteryColor,
                        )
                    }

                    // Show indication if there are more prize tiers
                    if (contest.prizeList.size > 4) {
                        Text(
                            text = "e mais ${contest.prizeList.size - 4} faixas...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = spacing.xs),
                        )
                    }
                }

                // Next Contest Prize Section
                if (contest.nextContestEstimatedPrize != null && contest.nextContestEstimatedPrize > 0) {
                    Spacer(modifier = Modifier.height(spacing.md))

                    // Visual Separator
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    val formattedPrize = com.cebolao.app.util.FormatUtils.formatCurrency(contest.nextContestEstimatedPrize)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Estimativa próximo concurso",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )

                        Spacer(modifier = Modifier.height(spacing.xs))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = formattedPrize,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = lotteryColor,
                            )

                            if (contest.nextContestDate != null) {
                                Text(
                                    text = contest.nextContestDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrizeTierRow(
    prize: com.cebolao.domain.model.Prize,
    lotteryColor: androidx.compose.ui.graphics.Color,
) {
    val spacing = LocalSpacing.current
    val formattedPrize = com.cebolao.app.util.FormatUtils.formatCurrency(prize.prizeValue)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Prize tier name/description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prize.description ?: prize.range,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        // Winners count
        Column(
            modifier = Modifier.weight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (prize.winners > 0) {
                Text(
                    text = "${prize.winners}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = lotteryColor,
                )
                Text(
                    text = if (prize.winners == 1) "ganhador" else "ganhadores",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "Acumulou",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        // Prize value
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
        ) {
            if (prize.prizeValue > 0) {
                Text(
                    text = formattedPrize,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun BallsLayout(
    numbers: List<Int>,
    type: LotteryType,
) {
    // Layout adaptativo: simula FlowRow com particionamento manual
    val chunkSize = 10
    val chunks = numbers.chunked(chunkSize)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunks.forEach { chunk ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chunk.forEach { number ->
                    SmallLotteryBall(number = number, lotteryType = type)
                }
            }
        }
    }
}
