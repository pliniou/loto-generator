package com.cebolao.domain.model

import kotlinx.serialization.Serializable

/**
 * Estrutura consolidada de dados salvos localmente.
 * Estrutura legada (formato antigo) usada para migração a partir de `lottery_data.json`.
 * Kotlin puro (sem Android).
 *
 * @property contests Concursos organizados por modalidade
 * @property games Jogos salvos pelo usuário
 * @property lastSyncTimestamp Timestamp da última sincronização por modalidade
 */
@Serializable
data class LotteryData(
    val contests: Map<LotteryType, List<Contest>> = emptyMap(),
    val games: List<Game> = emptyList(),
    val lastSyncTimestamp: Map<LotteryType, Long> = emptyMap(),
) {
    /**
     * Retorna último concurso de uma modalidade.
     */
    fun getLastContest(type: LotteryType): Contest? = contests[type]?.maxByOrNull { it.id }

    /**
     * Retorna todos os jogos de uma modalidade específica.
     */
    fun getGamesByType(type: LotteryType): List<Game> = games.filter { it.lotteryType == type }

    /**
     * Retorna jogos fixados primeiro, depois por data (mais recentes primeiro).
     */
    fun getSortedGames(): List<Game> =
        games.sortedWith(
            compareByDescending<Game> { it.isPinned }
                .thenByDescending { it.createdAt },
        )

    /**
     * Adiciona um novo jogo à lista.
     */
    fun addGame(game: Game): LotteryData = copy(games = games + game)

    /**
     * Insere ou atualiza um jogo (mesmo ID substitui).
     * Mantém a regra de unicidade do ID em um único ponto do domínio.
     */
    fun upsertGame(game: Game): LotteryData {
        val index = games.indexOfFirst { it.id == game.id }
        return if (index >= 0) {
            copy(games = games.toMutableList().apply { this[index] = game })
        } else {
            copy(games = games + game)
        }
    }

    /**
     * Insere ou atualiza vários jogos.
     */
    fun upsertGames(newGames: List<Game>): LotteryData = newGames.fold(this) { acc, game -> acc.upsertGame(game) }

    /**
     * Remove um jogo por ID.
     */
    fun removeGame(gameId: String): LotteryData = copy(games = games.filterNot { it.id == gameId })

    /**
     * Alterna o pin de um jogo.
     */
    fun toggleGamePin(gameId: String): LotteryData = copy(games = games.map { if (it.id == gameId) it.togglePin() else it })

    /**
     * Atualiza concursos de uma modalidade.
     */
    fun updateContests(
        type: LotteryType,
        newContests: List<Contest>,
    ): LotteryData =
        copy(
            contests = contests + (type to newContests),
            lastSyncTimestamp = lastSyncTimestamp + (type to System.currentTimeMillis()),
        )
}
