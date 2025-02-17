package dev.bltucker.spendless.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserPreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferences: UserPreferences)

    @Update
    suspend fun update(preferences: UserPreferences)

    @Delete
    suspend fun delete(preferences: UserPreferences)

    @Query("SELECT * FROM user_preferences WHERE user_id = :userId")
    suspend fun getPreferencesForUser(userId: Long): UserPreferences?

}