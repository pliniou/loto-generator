package com.cebolao.app.feature.generator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Tag
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.component.LotteryTypePillSelector
import com.cebolao.app.component.SmallLotteryBall
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.GenerationFilterUiMapper
import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.TimemaniaUtil
import kotlin.math.roundToInt

@Composable
fun GeneratorConfigSection(
    selectedType: LotteryType,
    quantity: Int,
    activeFilters: List<GenerationFilter>,
    filterConfigs: Map<GenerationFilter, FilterConfig>,
    profile: com.cebolao.domain.model.LotteryProfile?,
    onTypeSelected: (LotteryType) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onFilterToggled: (GenerationFilter) -> Unit,
    onOpenFilterConfig: () -> Unit,
    onInfoClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(selectedType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
            ),
        border = BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_LOW)),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = stringResource(R.string.generator_controls_heading),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = lotteryColor,
            )
            Text(
                text = stringResource(R.string.generator_controls_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.generator_select_lottery),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = lotteryColor,
                    )
                }
            }

            LotteryTypePillSelector(
                selectedType = selectedType,
                onTypeSelected = onTypeSelected,
                contentPadding = PaddingValues(vertical = spacing.xs),
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = stringResource(R.string.generator_quantity),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val totalCost = (profile?.costPerGame ?: 0) * quantity
                    val costFormatted = com.cebolao.app.util.FormatUtils.formatCurrency(totalCost.toLong())
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        AnimatedContent(
                            targetState = quantity,
                            label = "generator-quantity-content",
                        ) { currentQty ->
                            Text(
                                text = pluralStringResource(R.plurals.games_count, currentQty, currentQty),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                        Surface(
                            color = lotteryColor.copy(alpha = AlphaLevels.CARD_FAINT),
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                text = stringResource(R.string.generator_total_cost, costFormatted),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
                                color = lotteryColor,
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
                        enabled = quantity > 1,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = lotteryColor,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_MEDIUM),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                            ),
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(R.string.action_remove))
                    }

                    IconButton(
                        onClick = { if (quantity < 50) onQuantityChanged(quantity + 1) },
                        enabled = quantity < 50,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = lotteryColor,
                                contentColor = LotteryColors.getOnColor(selectedType),
                                disabledContainerColor = lotteryColor.copy(alpha = AlphaLevels.CARD_MEDIUM),
                                disabledContentColor = LotteryColors.getOnColor(selectedType).copy(alpha = AlphaLevels.TEXT_MEDIUM),
                            ),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_increase))
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
            )

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
                    TextButton(
                        onClick = onOpenFilterConfig,
                        modifier = Modifier.sizeIn(minHeight = 48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = lotteryColor,
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = stringResource(R.string.config_filters),
                            style = MaterialTheme.typography.labelLarge,
                            color = lotteryColor,
                        )
                    }
                }
            }

            if (activeFilters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = stringResource(R.string.generator_filters_active),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
                GeneratorActiveFiltersRow(
                    activeFilters = activeFilters,
                    filterConfigs = filterConfigs,
                    selectedType = selectedType,
                    onFilterToggled = onFilterToggled,
                )
            }

            val options =
                remember(profile) {
                    val base =
                        listOf(
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
private fun GeneratorActiveFiltersRow(
    activeFilters: List<GenerationFilter>,
    filterConfigs: Map<GenerationFilter, FilterConfig>,
    selectedType: LotteryType,
    onFilterToggled: (GenerationFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(selectedType)

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        items(items = activeFilters, key = { it.name }) { filter ->
            val cfg = filterConfigs[filter]
            val label =
                when (filter) {
                    GenerationFilter.PARITY_BALANCE -> {
                        val min = (cfg?.minParityRatio?.times(100))?.roundToInt() ?: 20
                        val max = (cfg?.maxParityRatio?.times(100))?.roundToInt() ?: 80
                        stringResource(R.string.filter_parity_range_short, min, max)
                    }
                    GenerationFilter.REPEATED_FROM_PREVIOUS -> {
                        val maxRepeats = cfg?.maxRepeatsFromPrevious ?: 4
                        stringResource(R.string.filter_repeats_limit_short, maxRepeats)
                    }
                    else -> {
                        stringResource(GenerationFilterUiMapper.getLabelRes(filter))
                    }
                }

            FilterChip(
                selected = true,
                onClick = { onFilterToggled(filter) },
                label = { Text(text = label) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
                modifier = Modifier.sizeIn(minHeight = 40.dp),
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = lotteryColor.copy(alpha = 0.16f),
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                        selectedLeadingIconColor = lotteryColor,
                    ),
                border =
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
                    ),
            )
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
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(stringResource(R.string.action_regenerate), fontWeight = FontWeight.Bold)
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
                        text = pluralStringResource(
                            R.plurals.generation_partial_warning,
                            quantity,
                            report.generated,
                            quantity,
                        ),
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
fun GeneratorPaginationControls(
    total: Int,
    page: Int,
    pageSize: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (total <= 0) return

    val spacing = LocalSpacing.current
    val safePageSize = pageSize.coerceAtLeast(1)
    val maxPage = (total - 1) / safePageSize
    val safePage = page.coerceIn(0, maxPage)

    val start = (safePage * safePageSize + 1).coerceAtMost(total)
    val end = ((safePage + 1) * safePageSize).coerceAtMost(total)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_LOW),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.generator_pagination_page, safePage + 1, maxPage + 1),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.generator_pagination_range, start, end, total),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPrevious,
                    enabled = safePage > 0,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_previous),
                    )
                }
                IconButton(
                    onClick = onNext,
                    enabled = safePage < maxPage,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.action_next),
                    )
                }
            }
        }
    }
}

@Composable
fun GeneratedGameItem(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    lastContest: com.cebolao.domain.model.Contest? = null,
) {
    val insight = remember(game, lastContest) { com.cebolao.domain.util.StatisticsUtil.analyzeGame(game.numbers, lastContest, emptyList()) }
    var animateIn by remember(game.id) { mutableStateOf(false) }

    LaunchedEffect(game.id) {
        animateIn = true
    }

    val model =
        remember(game, insight) {
            GeneratedGameCardModel(
                lotteryType = game.lotteryType,
                teamName = game.teamNumber?.let(TimemaniaUtil::getTeamName),
                numbers = game.numbers.sorted(),
                metrics =
                    GeneratedGameMetrics(
                        sum = insight.sum,
                        evenCount = insight.evenCount,
                        oddCount = insight.oddCount,
                        average = String.format(java.util.Locale.getDefault(), "%.1f", insight.average),
                        longestSequence = insight.longestSequence,
                        multiplesOf3 = insight.multiplesOf3,
                        primeCount = insight.primeCount,
                        repeatsFromLast = insight.repeatsFromLast,
                    ),
            )
        }

    AnimatedVisibility(
        visible = animateIn,
        enter = fadeIn(animationSpec = tween(durationMillis = 160)) + expandVertically(animationSpec = tween(durationMillis = 160)),
        exit = fadeOut(animationSpec = tween(durationMillis = 120)),
    ) {
        GeneratedGameCard(
            model = model,
            onClick = onClick,
            modifier = modifier,
        )
    }
}

private data class GeneratedGameCardModel(
    val lotteryType: LotteryType,
    val numbers: List<Int>,
    val teamName: String?,
    val metrics: GeneratedGameMetrics,
)

private data class GeneratedGameMetrics(
    val sum: Int,
    val evenCount: Int,
    val oddCount: Int,
    val average: String,
    val longestSequence: Int,
    val multiplesOf3: Int,
    val primeCount: Int,
    val repeatsFromLast: Int,
)

@Composable
private fun GeneratedGameCard(
    model: GeneratedGameCardModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(model.lotteryType)
    val numbersSequence = remember(model.numbers) { model.numbers.joinToString(separator = " ") { it.toString().padStart(2, '0') } }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(animationSpec = tween(durationMillis = 160))
                .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, lotteryColor.copy(alpha = AlphaLevels.BORDER_FAINT)),
    ) {
        Column(modifier = Modifier.padding(spacing.lg), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            if (model.teamName != null) {
                Text(
                    text = model.teamName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = lotteryColor,
                )
            }
            Text(
                text = stringResource(R.string.generator_number_sequence),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = numbersSequence,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )

            val chunks = model.numbers.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                chunks.forEach { chunk ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        chunk.forEach { number ->
                            SmallLotteryBall(number = number, lotteryType = model.lotteryType)
                        }
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AlphaLevels.BORDER_FAINT),
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.Top,
                ) {
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Functions,
                        label = stringResource(R.string.insight_label_sum),
                        value = model.metrics.sum.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.SwapVert,
                        label = stringResource(R.string.insight_label_even_odd),
                        value = "${model.metrics.evenCount}P/${model.metrics.oddCount}I",
                        containerColor = lotteryColor.copy(alpha = AlphaLevels.CARD_FAINT),
                        contentColor = lotteryColor,
                        isHighlighted = true,
                    )
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        label = stringResource(R.string.insight_label_average),
                        value = model.metrics.average,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.Top,
                ) {
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LinearScale,
                        label = stringResource(R.string.insight_label_longest_sequence),
                        value = model.metrics.longestSequence.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Percent,
                        label = stringResource(R.string.insight_label_multiples_of_3),
                        value = model.metrics.multiplesOf3.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InsightMetricChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Tag,
                        label = stringResource(R.string.insight_label_primes),
                        value = model.metrics.primeCount.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AlphaLevels.CARD_HIGH),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (model.metrics.repeatsFromLast > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InsightMetricChip(
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Repeat,
                            label = stringResource(R.string.insight_repeats_from_last),
                            value =
                                pluralStringResource(
                                    R.plurals.insight_repeats,
                                    model.metrics.repeatsFromLast,
                                    model.metrics.repeatsFromLast,
                                ),
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = AlphaLevels.CARD_MEDIUM),
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            isHighlighted = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightMetricChip(
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isHighlighted: Boolean = false,
) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = modifier.heightIn(min = 56.dp),
        color = containerColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = contentColor,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = AlphaLevels.TEXT_MEDIUM),
                )
                Text(
                    text = value,
                    style = if (isHighlighted) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall,
                    fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Medium,
                    color = contentColor,
                )
            }
        }
    }
}
