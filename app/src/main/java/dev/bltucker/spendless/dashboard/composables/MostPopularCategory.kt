package dev.bltucker.spendless.dashboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.Surface

@Composable
fun MostPopularCategory(modifier: Modifier = Modifier,
                        mostPopularCategory: TransactionCategory) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = Color(0x33FFFFFF) ),
        shape = RoundedCornerShape(16.dp),

        ) {
        Row(modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically){

            Box(
                modifier = Modifier.background(color = Color(0xFFEADDFF), shape = RoundedCornerShape(8.dp)).size(56.dp),
                contentAlignment = Alignment.Center,

            ){
                Text(mostPopularCategory.emoji, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top){

                Text(mostPopularCategory.displayName, style = MaterialTheme.typography.titleLarge, color = Surface)
                Text("Most Popular Category", style = MaterialTheme.typography.bodySmall, color = Surface)


            }
        }
    }
}


@Preview
@Composable
private fun MostPopularCategoryPreview() {
    SpendLessTheme {
        Box(modifier = Modifier.background(color = Primary).padding(16.dp)){
            MostPopularCategory(modifier = Modifier.fillMaxWidth(),
                mostPopularCategory = TransactionCategory.HOME)
        }
    }
}