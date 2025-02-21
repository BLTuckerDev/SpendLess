package dev.bltucker.spendless.common.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class RoomConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault()) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromRecurringFrequency(value: String?): RecurringFrequency? {
        return value?.let { RecurringFrequency.valueOf(it) }
    }

    @TypeConverter
    fun recurringFrequencyToString(frequency: RecurringFrequency?): String? {
        return frequency?.name
    }
}