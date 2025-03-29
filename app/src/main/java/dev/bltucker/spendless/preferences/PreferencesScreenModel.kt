package dev.bltucker.spendless.preferences

import dev.bltucker.spendless.common.room.UserPreferences

data class PreferencesScreenModel(
    val userId: Long? = null,
    val username: String? = null,
    val pin: String? = null,
    val isLoading: Boolean,
    val isError: Boolean,
    val useBracketsForExpense: Boolean,
    val currencySymbol: String,
    val decimalSeparator: String,
    val thousandsSeparator: String,
    val shouldNavToDashboard: Boolean = false,
    val shouldReauthenticate: Boolean = false,
){

    val formattedAmount = if(useBracketsForExpense){
        "(${currencySymbol}10${thousandsSeparator}382${decimalSeparator}45)"
    } else {
        "-${currencySymbol}10${thousandsSeparator}382${decimalSeparator}45"
    }

    val availableCurrencies = listOf(
        "$  US Dollar (USD)" to "$",
        "€  Euro (EUR)" to "€",
        "£  British Pound Sterling (GBP)" to "£",
        "¥  Japanese Yen (JPY)" to "¥",
        "CHF  Swiss Franc (CHF)" to "CHF",
        "C$  Canadian Dollar (CAD)" to "C$",
        "A$  Australian Dollar (AUD)" to "A$",
        "¥  Chinese Yuan Renminbi (CNY)" to "¥",
        "₹  Indian Rupee (INR)" to "₹",
        "R  South African Rand (ZAR)" to "R"
    )

    val availableThousandsSeparators = listOf(
        ".",
        ",",
        " ",
    )

    val availableDecimalSeparators = listOf(".", ",")


    fun getPreferencesEntity(): UserPreferences? {
        if(userId == null){
            return null
        }

        return UserPreferences(
            userId = userId,
            useBracketsForExpense = useBracketsForExpense,
            currencySymbol = currencySymbol,
            decimalSeparator = decimalSeparator,
            thousandsSeparator = thousandsSeparator,
        )
    }
}
