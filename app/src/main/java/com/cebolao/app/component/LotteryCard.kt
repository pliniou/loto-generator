package com.cebolao.app.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.CebolaoElevation
import com.cebolao.app.theme.ComponentDimensions
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
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

    // Animações de elevação e cor

    // Sem animação para valores constantes
    val elevation = CebolaoElevation.level2
    val borderColor = lotteryColor.copy(alpha = AlphaLevels.BORDER_MEDIUM)

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.sm),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_MEDIUM),
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation,
                pressedElevation = CebolaoElevation.level3,
                hoveredElevation = 3.dp, // Keeping slightly different hover if needed, or unify to level? Let's keep 3dp as level isn't exact or use level 2/3
            ),
        border =
            BorderStroke(
                width = ComponentDimensions.strokeWidthMedium,
                color = borderColor,
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_MEDIUM),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Visual Separator
                HorizontalDivider(
                    thickness = ComponentDimensions.dividerThicknessMedium,
                    color = lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT),
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
                        thickness = ComponentDimensions.dividerThicknessMedium,
                        color = lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT),
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    Text(
                        text = "Premiação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = lotteryColor,
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Show all prize tiers
                    contest.prizeList.forEachIndexed { index, prize ->
                        PrizeTierRow(
                            prize = prize,
                            lotteryColor = lotteryColor,
                            isMainPrize = index == 0,
                            isAccumulated = contest.accumulated,
                        )
                    }
                }

                // Next Contest Prize Section
                if (contest.nextContestEstimatedPrize != null && contest.nextContestEstimatedPrize > 0) {
                    Spacer(modifier = Modifier.height(spacing.md))

                    // Visual Separator
                    HorizontalDivider(
                        thickness = ComponentDimensions.dividerThicknessMedium,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_LOW),
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
    isMainPrize: Boolean,
    isAccumulated: Boolean,
) {
    val spacing = LocalSpacing.current
    val formattedPrize = com.cebolao.app.util.FormatUtils.formatCurrency(prize.prizeValue)

    Row(
        modifier =
            Modifier
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
                // Only show "Acumulou" if it's the main prize AND the contest is actually accumulated.
                // For other tiers with 0 winners, just show "0" or "Sem ganhadores" if preferred,
                // but usually "0" is clearer for secondary tiers.
                if (isMainPrize && isAccumulated) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(ComponentDimensions.iconSizeSmall),
                        )
                        Text(
                            text = "ACUMULOU",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                } else {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "ganhadores",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
