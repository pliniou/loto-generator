package com.cebolao.data.local.room

import androidx.room.TypeConverter
import com.cebolao.data.remote.dto.PrizeDto
import com.cebolao.domain.model.LotteryType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromLotteryType(value: LotteryType): String = value.name

    @TypeConverter
    fun toLotteryType(value: String): LotteryType = LotteryType.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    @TypeConverter
    fun fromPrizeList(value: List<PrizeDto>?): String {
        return if (value == null) "[]" else json.encodeToString(value)
    }

    @TypeConverter
    fun toPrizeList(value: String): List<PrizeDto>? {
        return try {
            json.decodeFromString<List<PrizeDto>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
