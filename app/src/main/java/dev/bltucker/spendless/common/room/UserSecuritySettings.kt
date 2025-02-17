package dev.bltucker.spendless.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "security_settings",
    foreignKeys = [
        ForeignKey(
            entity = SpendLessUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SecuritySettings(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "session_duration_minutes")
    val sessionDurationMinutes: Int = 5,

    @ColumnInfo(name = "lockout_duration_seconds")
    val lockoutDurationSeconds: Int = 30,

    @ColumnInfo(name = "biometrics_enabled")
    val biometricsEnabled: Boolean = false
)