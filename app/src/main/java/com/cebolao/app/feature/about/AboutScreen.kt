package com.cebolao.app.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.BuildConfig
import com.cebolao.R
import com.cebolao.app.feature.about.components.LotteryDetailedInfoCard
import com.cebolao.app.theme.LocalSpacing
import com.cebolao.app.ui.layout.CebolaoContent
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.util.LotteryInfoProvider

@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel(),
    isLargeScreen: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profiles = uiState.profiles
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    val expandedStates = remember { mutableStateMapOf<LotteryType, Boolean>() }
    val toggleExpansion: (LotteryType) -> Unit = remember(isLargeScreen) {
        { type ->
            if (isLargeScreen) {
                expandedStates[type] = !(expandedStates[type] ?: false)
            } else {
                val currentlyExpanded = expandedStates[type] == true
                expandedStates.clear()
                if (!currentlyExpanded) {
                    expandedStates[type] = true
                }
            }
        }
    }

    CebolaoContent {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AboutAppHeader(
                modifier = Modifier.fillMaxWidth(),
                versionName = BuildConfig.VERSION_NAME,
            )

            Spacer(modifier = Modifier.height(spacing.xxl))

            LotteryInfoList(
                profiles = profiles,
                isLargeScreen = isLargeScreen,
                expandedStates = expandedStates,
                onToggleExpansion = toggleExpansion,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            AboutContactSection(
                modifier = Modifier
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            Divider(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.lg),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp,
            )
            Spacer(modifier = Modifier.height(spacing.sm))

            Text(
                text = stringResource(R.string.about_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm),
            )
        }
    }
}

@Composable
private fun LotteryInfoList(
    profiles: List<LotteryProfile>,
    isLargeScreen: Boolean,
    expandedStates: Map<LotteryType, Boolean>,
    onToggleExpansion: (LotteryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val listModifier =
        modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg)

    Text(
        text = stringResource(R.string.about_lottery_info_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = listModifier.padding(bottom = spacing.md),
    )

    if (isLargeScreen && profiles.size > 1) {
        val splitIndex = (profiles.size + 1) / 2
        Row(
            modifier = listModifier,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            LotteryInfoColumn(
                profiles = profiles.take(splitIndex),
                expandedStates = expandedStates,
                onToggleExpansion = onToggleExpansion,
                modifier = Modifier.weight(1f),
            )
            LotteryInfoColumn(
                profiles = profiles.drop(splitIndex),
                expandedStates = expandedStates,
                onToggleExpansion = onToggleExpansion,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        LotteryInfoColumn(
            profiles = profiles,
            expandedStates = expandedStates,
            onToggleExpansion = onToggleExpansion,
            modifier = listModifier,
        )
    }
}

@Composable
private fun LotteryInfoColumn(
    profiles: List<LotteryProfile>,
    expandedStates: Map<LotteryType, Boolean>,
    onToggleExpansion: (LotteryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        profiles.forEach { profile ->
            val info = remember(profile.type) { LotteryInfoProvider.getInfo(profile.type) }
            LotteryDetailedInfoCard(
                profile = profile,
                info = info,
                isExpanded = expandedStates[profile.type] == true,
                onExpandClick = { onToggleExpansion(profile.type) },
            )
        }
    }
}

@Composable
private fun AboutAppHeader(
    modifier: Modifier = Modifier,
    versionName: String,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_about_banner),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    alpha = 0.85f,
                )
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    ),
                                ),
                            ),
                )
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = spacing.lg, bottom = spacing.lg),
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = stringResource(R.string.about_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.lg, vertical = spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.about_version, versionName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.about_header_status),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = spacing.sm),
                )
            }
        }
    }
}

@Composable
private fun AboutContactSection(
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val uriHandler = LocalUriHandler.current
    val contactEmailUri = stringResource(R.string.about_contact_email_uri)
    val contactSupportUrl = stringResource(R.string.about_contact_support_url)

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = stringResource(R.string.about_contact_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.about_contact_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            uriHandler.openUri(contactEmailUri)
                        }
                        .padding(vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.about_contact_email),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            uriHandler.openUri(contactSupportUrl)
                        }
                        .padding(vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.about_contact_support_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}
