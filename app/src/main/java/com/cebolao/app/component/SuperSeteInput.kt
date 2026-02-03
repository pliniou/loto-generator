package com.cebolao.app.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Layout com 7 colunas (0-6)
    val columns = 0..6

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(columns.toList()) { colIndex ->
            Column(
                modifier = Modifier.width(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Header (Coluna)
                Text(
                    text = "${colIndex + 1}",
                    fontWeight = FontWeight.Bold,
                    color = lotteryColor,
                    fontSize = 14.sp
                )

                (0..9).forEach { number ->
                    // Verifica se está selecionado. 
                    // Assume que selectedNumbers pode conter valores mapeados (col*10 + num) 
                    // ou se for lista posicional de 7 números (0-9)
                    val isSelected = if (selectedNumbers.size == 7 && selectedNumbers.all { it in 0..9 || it == -1 }) {
                         selectedNumbers.getOrNull(colIndex) == number
                    } else {
                         selectedNumbers.contains(colIndex * 10 + number)
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isSelected) lotteryColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp, 
                                color = if (isSelected) lotteryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 
                                shape = CircleShape
                            )
                            .clickable { onNumberClick(colIndex, number) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = number.toString(),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
