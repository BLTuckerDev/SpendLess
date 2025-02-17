package dev.bltucker.spendless.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SpendLessUserDao {
    @Insert
    suspend fun insert(user: SpendLessUser): Long

    @Update
    suspend fun update(user: SpendLessUser)

    @Delete
    suspend fun delete(user: SpendLessUser)

    @Query("SELECT * FROM spendless_users WHERE id = :id")
    suspend fun getUserById(id: Long): SpendLessUser?

    @Query("SELECT * FROM spendless_users WHERE username = :username")
    suspend fun getUserByUsername(username: String): SpendLessUser?

    @Query("SELECT EXISTS(SELECT 1 FROM spendless_users WHERE username = :username)")
    suspend fun doesUsernameExist(username: String): Boolean
}