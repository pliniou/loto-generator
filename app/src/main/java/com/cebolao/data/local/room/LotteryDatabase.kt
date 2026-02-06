package com.cebolao.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cebolao.data.local.room.dao.LotteryDao
import com.cebolao.data.local.room.entity.ContestEntity
import com.cebolao.data.local.room.entity.GameEntity

@Database(
    entities = [GameEntity::class, ContestEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class LotteryDatabase : RoomDatabase() {
    abstract fun lotteryDao(): LotteryDao
}
