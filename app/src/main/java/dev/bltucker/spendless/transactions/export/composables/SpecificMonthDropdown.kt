package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.transactions.export.SpecificMonthOption

@Composable
fun SpecificMonthDropdown(
    modifier: Modifier = Modifier,
    selectedMonth: SpecificMonthOption?,
    availableMonths: List<SpecificMonthOption>,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onMonthSelected: (SpecificMonthOption) -> Unit
) {
    val displayText = selectedMonth?.let { "${it.month} ${it.year}" } ?: "Select a month"

    DropdownSelector(
        modifier = modifier,
        selectedText = displayText,
        isExpanded = isExpanded,
        onToggleDropdown = onToggleDropdown
    ) {
        availableMonths.forEach { month ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = "${month.month} ${month.year}" +
                                if (month.isCurrentMonth) " • Current Month" else ""
                    )
                },
                onClick = { onMonthSelected(month) },
                trailingIcon = {
                    if (selectedMonth == month) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Primary
                        )
                    }
                }
            )
        }
    }
}