package com.cebolao.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.local.room.entity.GameEntity
import com.cebolao.domain.model.LotteryType
import kotlinx.coroutines.flow.Flow

@Dao
interface LotteryDao {
    // --- Games ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("SELECT * FROM games ORDER BY isPinned DESC, createdAt DESC")
    fun observeAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE lotteryType = :type ORDER BY isPinned DESC, createdAt DESC")
    fun observeGamesByType(type: LotteryType): Flow<List<GameEntity>>

    @Query("SELECT * FROM games ORDER BY isPinned DESC, createdAt DESC")
    suspend fun getAllGames(): List<GameEntity>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: String)

    @Query("DELETE FROM games WHERE lotteryType = :type")
    suspend fun deleteGamesByType(type: LotteryType)

    @Query("UPDATE games SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END WHERE id = :gameId")
    suspend fun toggleGamePin(gameId: String)

    // --- Contests ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContest(contest: ContestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContests(contests: List<ContestEntity>)

    @Query("SELECT * FROM contests WHERE lotteryType = :type ORDER BY contestNumber DESC LIMIT 1")
    fun observeLatestContest(type: LotteryType): Flow<ContestEntity?>

    @Query("SELECT * FROM contests WHERE lotteryType = :type ORDER BY contestNumber DESC")
    fun observeContests(type: LotteryType): Flow<List<ContestEntity>>

    @Query("SELECT * FROM contests WHERE lotteryType = :type ORDER BY contestNumber DESC LIMIT 1")
    suspend fun getLatestContest(type: LotteryType): ContestEntity?
}
