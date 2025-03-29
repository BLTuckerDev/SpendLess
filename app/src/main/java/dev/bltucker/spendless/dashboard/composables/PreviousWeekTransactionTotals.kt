package dev.bltucker.spendless.dashboard.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bltucker.spendless.common.theme.OnSurface
import dev.bltucker.spendless.common.theme.SecondaryContainer
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun PreviousWeekTransactionTotal(modifier: Modifier = Modifier,
                                 formattedTotal: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(containerColor = SecondaryContainer),
        shape = RoundedCornerShape(16.dp),

    ) {
        Column(modifier = Modifier.padding(8.dp)){
            Text(formattedTotal,
                color = OnSurface,
                style = MaterialTheme.typography.titleMedium)
            Text("Previous Week", color = OnSurface,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
private fun PreviousWeekTransactionTotalPreview(){
    SpendLessTheme {
        PreviousWeekTransactionTotal(
            modifier = Modifier.fillMaxWidth(),
            formattedTotal = "-$762.55"
        )
    }
}