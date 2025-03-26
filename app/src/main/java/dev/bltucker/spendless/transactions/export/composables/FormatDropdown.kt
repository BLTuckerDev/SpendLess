package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.transactions.export.ExportFormat

@Composable
fun FormatDropdown(
    modifier: Modifier = Modifier,
    selectedFormat: ExportFormat,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit
) {
    DropdownSelector(
        modifier = modifier,
        selectedText = selectedFormat.displayName,
        isExpanded = isExpanded,
        onToggleDropdown = onToggleDropdown
    ) {
        ExportFormat.values().forEach { format ->
            DropdownMenuItem(
                text = { Text(format.displayName) },
                onClick = { onFormatSelected(format) },
                trailingIcon = {
                    if (selectedFormat == format) {
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