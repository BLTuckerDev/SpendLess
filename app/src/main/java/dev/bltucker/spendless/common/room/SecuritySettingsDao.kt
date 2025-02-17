package dev.bltucker.spendless.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SecuritySettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: SecuritySettings)

    @Update
    suspend fun update(settings: SecuritySettings)

    @Delete
    suspend fun delete(settings: SecuritySettings)

    @Query("SELECT * FROM security_settings WHERE user_id = :userId")
    suspend fun getSettingsForUser(userId: Long): SecuritySettings?

}