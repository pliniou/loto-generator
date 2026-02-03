package com.cebolao.app.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.R
import com.cebolao.app.theme.AlphaLevels
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.domain.model.LotteryType

@Composable
fun SuperSeteInput(
    selectedNumbers: List<Int>,
    onNumberClick: (Int, Int) -> Unit,
) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(LotteryType.SUPER_SETE)

    // Layout com 7 colunas (0-9 cada)
    val columns = 0..6

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        columns.forEach { colIndex ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
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
