package com.cebolao.app.feature.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cebolao.R
import com.cebolao.app.theme.LocalSpacing
import kotlinx.coroutines.launch

data class OnboardPage(
    val titleRes: Int,
    val descRes: Int,
    val imageRes: Int,
)

@Composable
fun OnboardScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val pages =
        listOf(
            OnboardPage(R.string.onboard_title_1, R.string.onboard_desc_1, R.drawable.ic_onboard1),
            OnboardPage(R.string.onboard_title_2, R.string.onboard_desc_2, R.drawable.ic_onboard2),
            OnboardPage(R.string.onboard_title_3, R.string.onboard_desc_3, R.drawable.ic_onboard3),
            OnboardPage(R.string.onboard_title_4, R.string.onboard_desc_4, R.drawable.ic_onboard4),
        )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Páginas
            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) { pageIndex ->
                OnboardPageContent(page = pages[pageIndex])
            }

            // Controles
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Indicadores
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotSize by animateDpAsState(
                            targetValue = if (isSelected) 12.dp else 8.dp,
                            animationSpec = tween(durationMillis = 200),
                            label = "onboard-dot-size",
                        )
                        val dotColor by animateColorAsState(
                            targetValue =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                            animationSpec = tween(durationMillis = 200),
                            label = "onboard-dot-color",
                        )
                        Box(
                            modifier =
                                Modifier
                                    .size(dotSize)
                                    .clip(CircleShape)
                                    .background(dotColor),
                        )
                    }
                }

                // Botões
                val isLastPage = pagerState.currentPage == pages.size - 1

                Row {
                    if (!isLastPage) {
                        TextButton(onClick = {
                            scope.launch {
                                viewModel.completeOnboarding(onNavigateToHome)
                            }
                        }) {
                            Text(stringResource(R.string.onboard_skip))
                        }
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Button(onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }) {
                            Text(stringResource(R.string.onboard_next))
                        }
                    } else {
                        Button(onClick = {
                            viewModel.completeOnboarding(onNavigateToHome)
                        }) {
                            Text(stringResource(R.string.onboard_start))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardPageContent(page: OnboardPage) {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.xxl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(spacing.xxl))
        Text(
            text = stringResource(id = page.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = stringResource(id = page.descRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
