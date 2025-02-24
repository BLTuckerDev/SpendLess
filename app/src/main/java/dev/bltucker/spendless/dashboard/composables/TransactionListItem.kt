package dev.bltucker.spendless.common.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.Success
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest

@Composable
fun TransactionListItem(
    modifier: Modifier = Modifier,
    name: String,
    category: TransactionCategory,
    amount: String,
    isExpense: Boolean,
    note: String? = null,
    isSelected: Boolean = false,
    onItemClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceContainerLowest else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEADDFF)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.emoji,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                if (note != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.notes),
                            contentDescription = "Has note",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isSelected && !note.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = if (isExpense) MaterialTheme.colorScheme.error else Success
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionListItemPreview() {
    SpendLessTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TransactionListItem(
                name = "Starbucks",
                category = TransactionCategory.FOOD_AND_GROCERIES,
                amount = "-$7.50",
                isExpense = true,
                note = "Enjoyed a coffee and a snack at Starbucks with Rick and M.",
                isSelected = true,
                onItemClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            TransactionListItem(
                name = "Rick's share - Birthday M.",
                category = TransactionCategory.OTHER,
                amount = "$20.00",
                isExpense = false,
                onItemClick = {}
            )
        }
    }
}