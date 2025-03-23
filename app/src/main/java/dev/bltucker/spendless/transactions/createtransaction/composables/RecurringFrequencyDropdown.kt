package dev.bltucker.spendless.transactions.createtransaction.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.room.RecurringFrequency
import java.time.LocalDate
import java.util.Locale

@Composable
fun RecurringFrequencyDropdown(
    modifier: Modifier = Modifier,
    selectedFrequency: RecurringFrequency,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onFrequencySelected: (RecurringFrequency) -> Unit
) {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL,
        Locale.getDefault()
    )
    val dayOfMonth = today.dayOfMonth
    val month = today.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onToggleExpanded() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (selectedFrequency) {
                    RecurringFrequency.DOES_NOT_REPEAT -> "Does not repeat"
                    RecurringFrequency.DAILY -> "Daily"
                    RecurringFrequency.WEEKLY -> "Weekly on $dayOfWeek"
                    RecurringFrequency.MONTHLY -> "Monthly on the ${dayOfMonth}th"
                    RecurringFrequency.YEARLY -> "Yearly on $month $dayOfMonth"
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Recurring Frequency",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onToggleExpanded,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("Does not repeat") },
                onClick = { onFrequencySelected(RecurringFrequency.DOES_NOT_REPEAT) }
            )

            DropdownMenuItem(
                text = { Text("Daily") },
                onClick = { onFrequencySelected(RecurringFrequency.DAILY) }
            )

            DropdownMenuItem(
                text = { Text("Weekly on $dayOfWeek") },
                onClick = { onFrequencySelected(RecurringFrequency.WEEKLY) }
            )

            DropdownMenuItem(
                text = { Text("Monthly on the ${dayOfMonth}th") },
                onClick = { onFrequencySelected(RecurringFrequency.MONTHLY) }
            )

            DropdownMenuItem(
                text = { Text("Yearly on $month $dayOfMonth") },
                onClick = { onFrequencySelected(RecurringFrequency.YEARLY) }
            )
        }
    }
}