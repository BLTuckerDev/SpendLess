package dev.bltucker.spendless.dashboard

import dev.bltucker.spendless.common.room.SpendLessUser

data class DashboardScreenModel(
    val user: SpendLessUser? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    )