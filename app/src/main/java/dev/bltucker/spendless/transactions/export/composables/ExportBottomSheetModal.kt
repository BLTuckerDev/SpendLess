package dev.bltucker.spendless.transactions.export.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest
import dev.bltucker.spendless.transactions.export.ExportScreenContent
import dev.bltucker.spendless.transactions.export.ExportScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheetModal(
    userId: Long,
    onDismiss: () -> Unit
) {
    val viewModel = hiltViewModel<ExportScreenViewModel>()
    val model by viewModel.observableModel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onStart(userId)
    }

    LaunchedEffect(model.exportSuccessful) {
        if (model.exportSuccessful) {
            viewModel.onExportComplete()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = SurfaceContainerLowest,
    ) {
        ExportScreenContent(
            model = model,
            onCloseClick = onDismiss,
            onExportClick = viewModel::onExportClick,
            onExportDateRangeDropdownToggle = viewModel::onExportDateRangeDropdownToggle,
            onExportDateRangeSelected = viewModel::onExportDateRangeSelected,
            onFormatDropdownToggle = viewModel::onFormatDropdownToggle,
            onFormatSelected = viewModel::onFormatSelected,
            onSpecificMonthDropdownToggle = viewModel::onSpecificMonthDropdownToggle,
            onSpecificMonthSelected = viewModel::onSpecificMonthSelected
        )
    }
}