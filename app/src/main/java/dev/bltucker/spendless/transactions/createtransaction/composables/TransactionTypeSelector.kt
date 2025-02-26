package dev.bltucker.spendless.transactions.createtransaction.composables

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itextpdf.kernel.pdf.PdfName.Row
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    isExpense: Boolean,
    onTypeChanged: (Boolean) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
            .background(color = Color(0xFFEEE5FF), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        SegmentedButton(
            shape = RoundedCornerShape(12.dp),
            onClick = { onTypeChanged(true) },
            selected = isExpense,
            modifier = Modifier.weight(1f),
            border = BorderStroke(0.dp, Color.Transparent),
            colors = SegmentedButtonDefaults.colors(
                inactiveContainerColor = Color(0xFFEEE5FF),
                activeContainerColor = MaterialTheme.colorScheme.surface,
            ),
            icon = {},
        ) {
            Row{
                Icon(
                    painter = painterResource(R.drawable.trending_down),
                    contentDescription = "Expense",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(

                    text = "Expense",
                    color = Primary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        SegmentedButton(
            shape = RoundedCornerShape(12.dp),
            onClick = { onTypeChanged(false) },
            selected = !isExpense,
            modifier = Modifier.weight(1f),
            border = BorderStroke(0.dp, Color.Transparent),
            colors = SegmentedButtonDefaults.colors(
                inactiveContainerColor = Color(0xFFEEE5FF),
                activeContainerColor = MaterialTheme.colorScheme.surface,
            ),
            icon = {},
        ) {
            Row{
                Icon(
                    painter = painterResource(R.drawable.trending_up),
                    contentDescription = "Income",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Income",
                    color = Primary,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}


@Preview
@Composable
private fun TransactionTypeSelectorPreview(){
    SpendLessTheme {
        Column(modifier = Modifier.background(color = Color.White).padding(8.dp)){
            TransactionTypeSelector(modifier = Modifier.fillMaxWidth(),
                isExpense = true,
                onTypeChanged = {})

            Spacer(modifier = Modifier.height(8.dp))

            TransactionTypeSelector(modifier = Modifier.fillMaxWidth(),
                isExpense = false,
                onTypeChanged = {})
        }
    }
}
