package com.cebolao.app.feature.about.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.LotteryColors
import com.cebolao.domain.model.LotteryProfile
import java.util.Locale

@Composable
fun LotteryInfoCard(profile: LotteryProfile) {
    val spacing = LocalSpacing.current
    val lotteryColor = LotteryColors.getColor(profile.type)
    val priceFormat =
        remember(profile.costPerGame) {
            val format = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            format.format(profile.costPerGame / 100.0)
        }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = lotteryColor.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(1.dp, lotteryColor.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = lotteryColor,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.size(width = 4.dp, height = 24.dp),
                ) {}

                Spacer(modifier = Modifier.width(spacing.md))

                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = lotteryColor,
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))
            HorizontalDivider(thickness = 0.5.dp, color = lotteryColor.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(spacing.md))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem(label = stringResource(R.string.about_min_bet), value = priceFormat)
                InfoItem(label = stringResource(R.string.about_win_chance), value = profile.probabilityOfWinning.replace("1 em ", "1/"))
                InfoItem(label = stringResource(R.string.about_numbers), value = profile.numbersPerGame.toString())
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            if (profile.bolaoInfo != null) {
                BolaoInfoSection(profile.bolaoInfo, locale = Locale("pt", "BR"), brandColor = lotteryColor)
            } else {
                Text(
                    text = stringResource(R.string.about_no_pool),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = spacing.xs),
                )
            }
        }
    }
}

@Composable
fun BolaoInfoSection(
    info: com.cebolao.domain.model.BolaoInfo,
    locale: Locale,
    brandColor: Color,
) {
    val spacing = LocalSpacing.current
    val currencyFormat = remember(locale) { java.text.NumberFormat.getCurrencyInstance(locale) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = brandColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium,
                )
                .padding(spacing.md),
    ) {
        Text(
            text = stringResource(R.string.about_pool_rules),
            style = MaterialTheme.typography.labelMedium,
            color = brandColor,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoItemSmall(label = stringResource(R.string.about_pool_min_price), value = currencyFormat.format(info.minPoolPrice / 100.0))
            InfoItemSmall(label = stringResource(R.string.about_pool_min_share), value = currencyFormat.format(info.minSharePrice / 100.0))
            InfoItemSmall(label = stringResource(R.string.about_pool_shares), value = "${info.minShares}-${info.maxShares}")
        }
    }
}

@Composable
private fun InfoItemSmall(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
