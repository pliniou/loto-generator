package com.cebolao.domain.repository

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de dados de loteria.
 * Segue padrão offline-first com Flows reativos.
 */
interface LotteryRepository {
    /**
     * Observa concursos de uma modalidade.
     * Emite lista atualizada sempre que houver mudanças.
     */
    fun observeContests(type: LotteryType): Flow<List<Contest>>

    /**
     * Observa apenas o concurso mais recente de uma modalidade.
     *
     * Preferível para telas que precisam só do “último resultado” (ex.: Home),
     * pois evita carregar listas completas desnecessariamente.
     */
    fun observeLatestContest(type: LotteryType): Flow<Contest?>

    /**
     * Observa todos os jogos salvos.
     * Emite lista ordenada (fixados primeiro, depois por data).
     */
    fun observeGames(): Flow<List<Game>>

    /**
     * Observa jogos de uma modalidade específica.
     */
    fun observeGamesByType(type: LotteryType): Flow<List<Game>>

    /**
     * Retorna último concurso de uma modalidade.
     */
    suspend fun getLastContest(type: LotteryType): AppResult<Contest?>

    /**
     * Retorna os últimos N concursos de uma modalidade para cálculos estatísticos.
     */
    suspend fun getRecentContests(type: LotteryType, limit: Int): AppResult<List<Contest>>

    /**
     * Salva um novo jogo.
     */
    suspend fun saveGame(game: Game): AppResult<Unit>

    /**
     * Salva (insere ou atualiza) uma lista de jogos.
     */
    suspend fun saveGames(games: List<Game>): AppResult<Unit>

    /**
     * Remove um jogo por ID.
     */
    suspend fun deleteGame(gameId: String): AppResult<Unit>

    /**
     * Remove todos os jogos de uma modalidade.
     */
    suspend fun deleteAllGames(type: LotteryType): AppResult<Unit>

    /**
     * Alterna o pin de um jogo.
     */
    suspend fun togglePinGame(gameId: String): AppResult<Unit>

    /**
     * Atualiza concursos de uma modalidade.
     * Usado pela sincronização remota (marco 3).
     */
    suspend fun updateContests(
        type: LotteryType,
        newContests: List<Contest>,
    ): AppResult<Unit>

    /**
     * Força reload dos dados do disco.
     */
    suspend fun refresh(): AppResult<Unit>
}
