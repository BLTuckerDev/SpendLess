package dev.bltucker.spendless.transactions.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.composables.ErrorBanner
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest
import kotlinx.serialization.Serializable

@Serializable
data class ExportScreenNavArgs(
    val userId: Long
)

fun NavGraphBuilder.exportScreen(
    onNavigateBack: () -> Unit
) {
    composable<ExportScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<ExportScreenNavArgs>()
        val viewModel = hiltViewModel<ExportScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.userId)
            onStopOrDispose {  }
        }

        ExportScreenContent(
            model = model,
            onCloseClick = onNavigateBack,
            onExportDateRangeDropdownToggle = viewModel::onExportDateRangeDropdownToggle,
            onExportDateRangeSelected = viewModel::onExportDateRangeSelected,
            onFormatDropdownToggle = viewModel::onFormatDropdownToggle,
            onFormatSelected = viewModel::onFormatSelected,
            onSpecificMonthDropdownToggle = viewModel::onSpecificMonthDropdownToggle,
            onSpecificMonthSelected = viewModel::onSpecificMonthSelected,
            onExportClick = viewModel::onExportClick
        )

        LaunchedEffect(model.exportSuccessful) {
            if (model.exportSuccessful) {
                viewModel.onExportComplete()
                onNavigateBack()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreenContent(
    modifier: Modifier = Modifier,
    model: ExportScreenModel,
    onCloseClick: () -> Unit,
    onExportDateRangeDropdownToggle: () -> Unit,
    onExportDateRangeSelected: (ExportDateRange) -> Unit,
    onFormatDropdownToggle: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit,
    onSpecificMonthDropdownToggle: () -> Unit,
    onSpecificMonthSelected: (SpecificMonthOption) -> Unit,
    onExportClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLowest
        )
    ) {
        Column {
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
                    onClick = onCloseClick,
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

            // Export Format dropdown (MVP doesn't require this)
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

            Spacer(modifier = Modifier.weight(1f))

            // Export Button
            Button(
                onClick = onExportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

@Composable
fun DropdownSelector(
    modifier: Modifier = Modifier,
    selectedText: String,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    content: @Composable () -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Dropdown Arrow Animation"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = onToggleDropdown
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle dropdown",
                    modifier = Modifier.rotate(rotationState)
                )
            }
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onToggleDropdown,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(SurfaceContainerLowest)
                .align(Alignment.TopStart)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExportScreenPreview() {
    SpendLessTheme {
        ExportScreenContent(
            model = ExportScreenModel(
                userId = 1L,
                isLoading = false,
                exportDateRange = ExportDateRange.LAST_THREE_MONTHS,
                exportFormat = ExportFormat.CSV,
                availableMonths = listOf(
                    SpecificMonthOption("February", 2025, true),
                    SpecificMonthOption("January", 2025),
                    SpecificMonthOption("December", 2024)
                )
            ),
            onCloseClick = { },
            onExportDateRangeDropdownToggle = { },
            onExportDateRangeSelected = { },
            onFormatDropdownToggle = { },
            onFormatSelected = { },
            onSpecificMonthDropdownToggle = { },
            onSpecificMonthSelected = { },
            onExportClick = { }
        )
    }
}