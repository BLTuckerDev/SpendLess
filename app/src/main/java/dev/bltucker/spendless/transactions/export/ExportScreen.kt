package dev.bltucker.spendless.transactions.export

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest
import dev.bltucker.spendless.transactions.export.composables.DateRangeDropdown
import dev.bltucker.spendless.transactions.export.composables.FormatDropdown
import dev.bltucker.spendless.transactions.export.composables.SpecificMonthDropdown
import kotlinx.serialization.Serializable


@Composable
fun ExportScreenWithPermissions(
    modifier: Modifier = Modifier,
    model: ExportScreenModel,
    onNavigateBack: () -> Unit,
    onExportClick: () -> Unit,
    onExportDateRangeDropdownToggle: () -> Unit,
    onExportDateRangeSelected: (ExportDateRange) -> Unit,
    onFormatDropdownToggle: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit,
    onSpecificMonthDropdownToggle: () -> Unit,
    onSpecificMonthSelected: (SpecificMonthOption) -> Unit,
) {
    val context = LocalContext.current

    // Check if we need runtime permission
    val needsPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q


    var hasPermission by remember {
        mutableStateOf(
            !needsPermission || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
    }

    if (!hasPermission) {
        PermissionRequest(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        )
    } else {
        ExportScreenContent(
            modifier = modifier,
            model = model,
            onCloseClick = onNavigateBack,
            onExportClick = onExportClick,
            onExportDateRangeDropdownToggle = onExportDateRangeDropdownToggle,
            onExportDateRangeSelected = onExportDateRangeSelected,
            onFormatDropdownToggle = onFormatDropdownToggle,
            onFormatSelected = onFormatSelected,
            onSpecificMonthDropdownToggle = onSpecificMonthDropdownToggle,
            onSpecificMonthSelected = onSpecificMonthSelected
        )
    }
}

@Composable
fun PermissionRequest(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Storage Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SpendLess needs permission to save exported files to your Downloads folder.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            )
        ) {
            Text(
                text = "Grant Permission",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
        if (model.isLoading) {
            LoadingSpinner(modifier = Modifier.fillMaxWidth())
        } else {
            Column(modifier = Modifier) {
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

                Spacer(modifier = Modifier.height(24.dp))

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