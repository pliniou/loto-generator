package com.cebolao.app.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.domain.model.LotteryType

@Composable
fun SuperSeteInput(
    selectedNumbers: List<Int>, // Deve ter tamanho 7
    onNumberClick: (colIndex: Int, number: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val color = LotteryColors.getColor(LotteryType.SUPER_SETE)
    val onColor = LotteryColors.getOnColor(LotteryType.SUPER_SETE)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.super_sete_select_number),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.xs),
        )

        LazyRow(
            contentPadding = PaddingValues(vertical = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            items(7) { colIndex ->
                SuperSeteColumn(
                    colIndex = colIndex,
                    selectedNumber = selectedNumbers.getOrNull(colIndex) ?: -1,
                    onNumberClick = { num -> onNumberClick(colIndex, num) },
                    color = color,
                    onColor = onColor,
                )
            }
        }
    }
}

@Composable
fun SuperSeteColumn(
    colIndex: Int,
    selectedNumber: Int,
    onNumberClick: (Int) -> Unit,
    color: Color,
    onColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.super_sete_column, colIndex + 1),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )

        // Números 0 a 9
        (0..9).forEach { number ->
            val isSelected = (number == selectedNumber)
            Box(
                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) color else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape,
                        )
                        .clickable { onNumberClick(number) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    color = if (isSelected) onColor else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
