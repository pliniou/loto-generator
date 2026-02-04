package com.cebolao.app.feature.generator.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.SmallLotteryBall
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.AnimationDurations
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.GenerationFilterUiMapper
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.TimemaniaUtil

@Composable
fun GeneratorConfigSection(
    selectedType: LotteryType,
    quantity: Int,
    activeFilters: List<GenerationFilter>,
    profile: com.cebolao.domain.model.LotteryProfile?,
    onTypeSelected: (LotteryType) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onFilterToggled: (GenerationFilter) -> Unit,
    onOpenFilterConfig: () -> Unit,
    onInfoClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(selectedType)

    // 1. Seletor de Modalidade
    // 1. Seletor de Modalidade
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.generator_select_lottery),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Informações da Loteria",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.padding(bottom = spacing.lg),
    ) {
        items(items = LotteryType.entries, key = { it.name }) { type ->
            val isSelected = type == selectedType
            val chipColor by animateColorAsState(
                targetValue = LotteryColors.getColor(type),
                animationSpec = tween(durationMillis = AnimationDurations.medium),
                label = "chip-color-$type"
            )
            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                label = { Text(stringResource(LotteryUiMapper.getNameRes(type))) },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
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

    // 2. Configurações
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
            ),
        border = BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_LOW)),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            // Quantidade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.generator_quantity),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = pluralStringResource(R.plurals.games_count, quantity, quantity),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
                        enabled = quantity > 1,
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = lotteryColor,
                            ),
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(R.string.action_remove))
                    }

                    IconButton(
                        onClick = { if (quantity < 50) onQuantityChanged(quantity + 1) },
                        enabled = quantity < 50,
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = lotteryColor,
                                contentColor = LotteryColors.getOnColor(selectedType),
                            ),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_increase))
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Custo estimado com visual mais limpo
            val totalCost = (profile?.costPerGame ?: 0) * quantity
            val costFormatted = com.cebolao.app.util.FormatUtils.formatCurrency(totalCost.toLong())

            Surface(
                color = lotteryColor.copy(alpha = AlphaLevels.CARD_FAINT),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = stringResource(R.string.generator_total_cost, costFormatted),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                    color = lotteryColor,
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT)
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Filtros configuráveis
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.generator_filters),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )

                // Badge com contador
                BadgedBox(
                    badge = {
                        if (activeFilters.isNotEmpty()) {
                            Badge(
                                containerColor = lotteryColor,
                                contentColor = LotteryColors.getOnColor(selectedType),
                            ) {
                                Text(
                                    text = activeFilters.size.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    },
                ) {
                    TextButton(onClick = onOpenFilterConfig) {
                        Text(
                            text = stringResource(R.string.config_filters),
                            style = MaterialTheme.typography.labelLarge,
                            color = lotteryColor,
                        )
                    }
                }
            }

            val options = remember(profile) {
                val base = listOf(
                    GenerationFilter.PARITY_BALANCE,
                    GenerationFilter.MULTIPLES_OF_3,
                    GenerationFilter.REPEATED_FROM_PREVIOUS,
                    GenerationFilter.MOLDURA_MIOLO,
                    GenerationFilter.PRIME_NUMBERS,
                )
                profile?.let { p -> base.filter { it.isApplicable(p) } } ?: base
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(items = options, key = { it.name }) { filter ->
                    val isActive = activeFilters.contains(filter)
                    FilterChip(
                        selected = isActive,
                        onClick = { onFilterToggled(filter) },
                        label = { Text(stringResource(GenerationFilterUiMapper.getLabelRes(filter))) },
                        modifier = Modifier.sizeIn(minHeight = 48.dp),
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = lotteryColor,
                                selectedLabelColor = LotteryColors.getOnColor(selectedType),
                            ),
                        border =
                            if (isActive) {
                                null
                            } else {
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                )
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun TimemaniaTeamCard(
    selectedType: LotteryType,
    selectedTeam: Int?,
    onShowTeamDialog: () -> Unit,
) {
    val spacing = LocalSpacing.current
    if (selectedType == LotteryType.TIMEMANIA) {
        val lotteryColor = LotteryColors.getColor(LotteryType.TIMEMANIA)
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.lg)
                    .semantics { role = Role.Button }
                    .clickable { onShowTeamDialog() },
            shape = MaterialTheme.shapes.large,
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
                ),
            border = BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_LOW)),
        ) {
            Row(
                modifier = Modifier.padding(spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.timemania_team_optional),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text =
                            selectedTeam?.let { TimemaniaUtil.getTeamName(it) }
                                ?: stringResource(R.string.timemania_random_team_saved),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.action_select),
                    tint = lotteryColor,
                )
            }
        }
    }
}

@Composable
fun GeneratorResultsSection(
    generatedCount: Int,
    quantity: Int,
    report: com.cebolao.domain.model.GenerationReport?,
    onOpenReportDetails: () -> Unit,
    onRetry: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = pluralStringResource(R.plurals.games_generated_count, generatedCount, generatedCount),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        if (generatedCount > 0) {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_redo), fontWeight = FontWeight.Bold)
            }
        }
    }


    // Banner de geração parcial
    if (report != null && report.partial) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = spacing.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(modifier = Modifier.padding(spacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = stringResource(R.string.generation_partial_warning, report.generated, quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
                Spacer(modifier = Modifier.height(spacing.sm))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onOpenReportDetails) {
                        Text(text = stringResource(R.string.report_details), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratedGameItem(
    game: Game,
    lastContest: com.cebolao.domain.model.Contest? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(game.lotteryType)

    // Calculate insights on the fly (lightweight)
    val insight = remember(game, lastContest) { com.cebolao.domain.util.StatisticsUtil.analyzeGame(game.numbers, lastContest, emptyList()) }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT)),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            // Se tiver time (Timemania), mostra cabeçalho ou badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (game.teamNumber != null) {
                        Text(
                            text = TimemaniaUtil.getTeamName(game.teamNumber),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = lotteryColor,
                            modifier = Modifier.padding(bottom = spacing.sm),
                        )
                    }

                    // Layout adaptativo para os números
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
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT)
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Insights Row com badges coloridas
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                // Primeira linha de insights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InsightBadge(
                        label = stringResource(R.string.insight_sum, insight.sum),
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    InsightBadge(
                        label = "${insight.evenCount}P / ${insight.oddCount}I",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    InsightBadge(
                        label = "Média: ${insight.average}",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }

                // Segunda linha de insights detalhados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InsightBadge(
                        label = "Seq: ${insight.longestSequence}",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InsightBadge(
                        label = "Múlt3: ${insight.multiplesOf3}",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InsightBadge(
                        label = "Primos: ${insight.primeCount}",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Terceira linha com repetições
                if (insight.repeatsFromLast > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InsightBadge(
                            label = stringResource(R.string.insight_repeats, insight.repeatsFromLast),
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightBadge(
    label: String,
    containerColor: Color,
    contentColor: Color,
) {
    val spacing = LocalSpacing.current
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            color = contentColor,
        )
    }
}
