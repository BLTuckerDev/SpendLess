package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest
import dev.bltucker.spendless.transactions.export.ExportDateRange
import dev.bltucker.spendless.transactions.export.ExportFormat
import dev.bltucker.spendless.transactions.export.ExportScreenModel
import dev.bltucker.spendless.transactions.export.SpecificMonthOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    model: ExportScreenModel,
    onDismiss: () -> Unit,
    onExportDateRangeDropdownToggle: () -> Unit,
    onExportDateRangeSelected: (ExportDateRange) -> Unit,
    onFormatDropdownToggle: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit,
    onSpecificMonthDropdownToggle: () -> Unit,
    onSpecificMonthSelected: (SpecificMonthOption) -> Unit,
    onExportClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = SurfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Sheet Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Export",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Format text
            Text(
                text = "Export transactions to CSV format",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Date Range Dropdown
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Export Range",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                DateRangeDropdown(
                    selectedRange = model.exportDateRange,
                    isExpanded = model.isDateRangeDropdownExpanded,
                    onToggleDropdown = onExportDateRangeDropdownToggle,
                    onOptionSelected = onExportDateRangeSelected
                )
            }

            // Specific Month dropdown (only shown if Specific Month is selected)
            AnimatedVisibility(
                visible = model.exportDateRange == ExportDateRange.SPECIFIC_MONTH,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    SpecificMonthDropdown(
                        selectedMonth = model.selectedSpecificMonth,
                        availableMonths = model.availableMonths,
                        isExpanded = model.isSpecificMonthDropdownExpanded,
                        onToggleDropdown = onSpecificMonthDropdownToggle,
                        onMonthSelected = onSpecificMonthSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Export Format dropdown
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Export format",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormatDropdown(
                    selectedFormat = model.exportFormat,
                    isExpanded = model.isFormatDropdownExpanded,
                    onToggleDropdown = onFormatDropdownToggle,
                    onFormatSelected = onFormatSelected
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Export Button
            Button(
                onClick = onExportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = "Export",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (model.isLoading) {
        LoadingSpinner()
    }
}