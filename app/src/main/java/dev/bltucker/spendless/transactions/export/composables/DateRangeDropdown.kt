package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.transactions.export.ExportDateRange

@Composable
fun DateRangeDropdown(
    modifier: Modifier = Modifier,
    selectedRange: ExportDateRange,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onOptionSelected: (ExportDateRange) -> Unit
) {
    DropdownSelector(
        modifier = modifier,
        selectedText = selectedRange.displayName,
        isExpanded = isExpanded,
        onToggleDropdown = onToggleDropdown
    ) {
        ExportDateRange.values().forEach { range ->
            DropdownMenuItem(
                text = { Text(range.displayName) },
                onClick = { onOptionSelected(range) },
                trailingIcon = {
                    if (selectedRange == range) {
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