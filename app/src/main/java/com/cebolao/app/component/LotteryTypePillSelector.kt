package com.cebolao.app.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cebolao.domain.model.LotteryType

@Composable
fun LotteryTypePillSelector(
    selectedType: LotteryType,
    onTypeSelected: (LotteryType) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LotteryFilterBar(
        selectedType = selectedType,
        onSelectionChanged = { type -> type?.let(onTypeSelected) },
        modifier = modifier,
        includeAllOption = false,
        showSelectedCheck = true,
        contentPadding = contentPadding,
    )
}
