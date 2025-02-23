package dev.bltucker.spendless.dashboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun AccountBalance(modifier: Modifier = Modifier,
                      accountBalance: String,) {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        Text(text = accountBalance, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onPrimary)
        Text(text = "Account Balance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary)

    }
}


@Preview
@Composable
private fun AccountBalancePreview(){
    SpendLessTheme {
        Surface(
            color = Primary){
            AccountBalance(accountBalance = "$10,345.55")
        }
    }
}