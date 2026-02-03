package com.cebolao.domain.model

/**
 * Relatório simplificado do processo de geração.
 */
data class GenerationReport(
    val attempts: Int,
    val generated: Int,
    val rejectedByFilter: Int,
    val rejectedPerFilter: Map<GenerationFilter, Int> = emptyMap(),
    val rejectedExamples: Map<GenerationFilter, List<List<Int>>> = emptyMap(),
    val partial: Boolean,
)

/**
 * Resultado da geração: jogos + relatório
 */
data class GenerationResult(
    val games: List<Game>,
    val report: GenerationReport,
)
