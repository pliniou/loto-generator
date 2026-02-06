package com.cebolao.app.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.theme.LotteryColors
import com.cebolao.app.util.LotteryUiMapper
import com.cebolao.domain.model.LotteryType

@Composable
fun LotteryFilterBar(
    selectedType: LotteryType?,
    onSelectionChanged: (LotteryType?) -> Unit,
    modifier: Modifier = Modifier,
    includeAllOption: Boolean = false,
    totalCount: Int? = null,
    countsByType: Map<LotteryType, Int> = emptyMap(),
    showSelectedCheck: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val spacing = LocalSpacing.current

    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        if (includeAllOption) {
            item {
                val isSelected = selectedType == null
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectionChanged(null) },
                    label = {
                        Text(
                            text =
                                buildFilterLabel(
                                    baseLabel = stringResource(R.string.filter_all),
                                    count = totalCount,
                                ),
                        )
                    },
                    leadingIcon =
                        if (showSelectedCheck && isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        } else {
                            null
                        },
                    modifier = Modifier.sizeIn(minHeight = 48.dp),
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                )
            }
        }

        items(items = LotteryType.entries, key = { it.name }) { type ->
            val isSelected = type == selectedType
            val chipColor = LotteryColors.getColor(type)
            val label =
                buildFilterLabel(
                    baseLabel = stringResource(LotteryUiMapper.getNameRes(type)),
                    count = countsByType[type],
                )

            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected && includeAllOption) {
                        onSelectionChanged(null)
                    } else {
                        onSelectionChanged(type)
                    }
                },
                label = {
                    Text(
                        text = label,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                leadingIcon =
                    if (showSelectedCheck && isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    } else {
                        null
                    },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipColor,
                        selectedLabelColor = LotteryColors.getOnColor(type),
                        selectedLeadingIconColor = LotteryColors.getOnColor(type),
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
}

@Composable
private fun buildFilterLabel(
    baseLabel: String,
    count: Int?,
): String =
    if (count == null) {
        baseLabel
    } else {
        stringResource(R.string.filter_label_with_count, baseLabel, count)
    }
