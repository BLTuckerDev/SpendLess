package dev.bltucker.spendless.dashboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.Surface

@Composable
fun LargestTransactionItem(modifier: Modifier = Modifier,
                           transactionTitle: String,
                           formattedTransactionAmount: String,
                           formattedTransactionDate: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = Color(0xFFEADDFF) ),
        shape = RoundedCornerShape(16.dp),

        ) {
        Row(modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically){

            Column(horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top){

                Text(transactionTitle, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text("Largest Transaction", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiary)


            }

            Spacer(modifier = Modifier.weight(1F))

            Column(horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top){

                Text(formattedTransactionAmount, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(formattedTransactionDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiary)


            }
        }
    }
}


@Composable
@Preview
private fun LargestTransactionItemPreview(){
    SpendLessTheme {
        Box(modifier = Modifier.background(color = Primary).padding(16.dp)) {
            LargestTransactionItem(modifier = Modifier.fillMaxWidth(),
                transactionTitle = "Groceries",
                formattedTransactionAmount = "-$762.55",
                formattedTransactionDate = "Jan 7, 2025")
        }
    }
}