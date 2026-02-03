package com.cebolao.app.util

import androidx.annotation.StringRes
import com.cebolao.R
import com.cebolao.domain.model.GenerationFilter

/**
 * Mapeia filtros de geração para recursos de texto.
 */
object GenerationFilterUiMapper {
    @StringRes
    fun getLabelRes(filter: GenerationFilter): Int =
        when (filter) {
            GenerationFilter.PARITY_BALANCE -> R.string.filter_parity_label
            GenerationFilter.MULTIPLES_OF_3 -> R.string.filter_multiples_label
            GenerationFilter.REPEATED_FROM_PREVIOUS -> R.string.filter_repeated_label
            GenerationFilter.MOLDURA_MIOLO -> R.string.filter_moldura_label
            GenerationFilter.PRIME_NUMBERS -> R.string.filter_primes_label
        }

    @StringRes
    fun getDescriptionRes(filter: GenerationFilter): Int =
        when (filter) {
            GenerationFilter.PARITY_BALANCE -> R.string.filter_parity_desc
            GenerationFilter.MULTIPLES_OF_3 -> R.string.filter_multiples_desc
            GenerationFilter.REPEATED_FROM_PREVIOUS -> R.string.filter_repeated_desc
            GenerationFilter.MOLDURA_MIOLO -> R.string.filter_moldura_desc
            GenerationFilter.PRIME_NUMBERS -> R.string.filter_primes_desc
        }
}
