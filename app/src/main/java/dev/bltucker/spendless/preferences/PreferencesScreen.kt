package dev.bltucker.spendless.preferences

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.composables.ErrorScreen
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.preferences.composables.AmountSpentCard
import dev.bltucker.spendless.preferences.composables.CurrencyDropdown
import dev.bltucker.spendless.preferences.composables.DecimalSeparatorSelector
import dev.bltucker.spendless.preferences.composables.ExpenseFormatSelector
import dev.bltucker.spendless.preferences.composables.ThousandsSeparatorSelector
import kotlinx.serialization.Serializable


@Serializable
data class PreferencesScreenNavArgs(
    val userId: Long?,
    val username: String? = null,
    val pin: String? = null,
)

fun NavGraphBuilder.preferencesScreen(
    onNavigateBack: () -> Unit,
    onNavigateBackToPinCreate: (String) -> Unit,
    onNavigateToDashboard: (Long) -> Unit,
) {
    composable<PreferencesScreenNavArgs> { backStackEntry ->
        val args = backStackEntry.toRoute<PreferencesScreenNavArgs>()
        val viewModel: PreferencesScreenViewModel = hiltViewModel<PreferencesScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        BackHandler {
            if(args.userId == null && args.username != null){
                onNavigateBackToPinCreate(args.username)
            } else {
                onNavigateBack()
            }
        }

        LaunchedEffect(model.shouldNavToDashboard) {
            if(model.shouldNavToDashboard){
                val safeUserId = model.userId
                safeUserId?.let{
                    onNavigateToDashboard(safeUserId)
                }
            }
        }

        //if we have a user id then the user has been created
        //if we dont, then we must have username and pin
        //if we are missing data -> error state
        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.userId, args.username, args.pin)

            onStopOrDispose {  }
        }


        PreferencesScreenContent(
            model = model,
            onNavigateBack = {
                if(args.userId == null && args.username != null){
                    onNavigateBackToPinCreate(args.username)
                } else {
                    onNavigateBack()
                }
            },
            onUseBracketsChange = viewModel::onUseBracketsChange,
            onCurrencyChange = viewModel::onCurrencyChange,
            onDecimalSeparatorChange = viewModel::onDecimalSeparatorChange,
            onThousandsSeparatorChange = viewModel::onThousandsSeparatorChange,
            onStartTrackingClick = viewModel::onStartTrackingClick,
            onSaveClick = viewModel::onSaveClick
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencesScreenContent(
    modifier: Modifier = Modifier,
    model: PreferencesScreenModel,
    onNavigateBack: () -> Unit,
    onUseBracketsChange: (Boolean) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onDecimalSeparatorChange: (String) -> Unit,
    onThousandsSeparatorChange: (String) -> Unit,
    onStartTrackingClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        when{
            model.isLoading -> LoadingSpinner(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues))
            model.isError -> ErrorScreen(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues))
            else -> PreferencesScreenColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                model = model,
                onUseBracketsChange = onUseBracketsChange,
                onCurrencyChange = onCurrencyChange,
                onDecimalSeparatorChange = onDecimalSeparatorChange,
                onThousandsSeparatorChange = onThousandsSeparatorChange,
                onStartTrackingClick = onStartTrackingClick,
                onSaveClick = onSaveClick,

            )
        }
    }
}


@Composable
private fun PreferencesScreenColumn(
    modifier: Modifier = Modifier,
    model: PreferencesScreenModel,
    onUseBracketsChange: (Boolean) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onDecimalSeparatorChange: (String) -> Unit,
    onThousandsSeparatorChange: (String) -> Unit,
    onStartTrackingClick: () -> Unit,
    onSaveClick: () -> Unit,
){
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Set SpendLess\nto your preferences",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You can change it at any time in Settings",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))


        AmountSpentCard(
            amount = model.formattedAmount
        )

        Spacer(modifier = Modifier.height(24.dp))


        ExpenseFormatSelector(
            modifier = Modifier.fillMaxWidth(),
            useBrackets = model.useBracketsForExpense,
            onFormatChange = onUseBracketsChange
        )

        Spacer(modifier = Modifier.height(16.dp))


        CurrencyDropdown(
            currencyList = model.availableCurrencies,
            selectedCurrency = model.currencySymbol,
            onCurrencySelected = onCurrencyChange
        )

        Spacer(modifier = Modifier.height(16.dp))


        DecimalSeparatorSelector(
            modifier = Modifier.fillMaxWidth(),
            selectedSeparator = model.decimalSeparator,
            onSeparatorSelected = onDecimalSeparatorChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThousandsSeparatorSelector(
            modifier = Modifier.fillMaxWidth(),
            selectedSeparator = model.thousandsSeparator,
            onSeparatorSelected = onThousandsSeparatorChange
        )

        Spacer(modifier = Modifier.weight(1f))

        if(model.userId != null){
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            Button(
                onClick = onStartTrackingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = "Start Tracking!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenContentPreview() {
    SpendLessTheme {
        PreferencesScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = PreferencesScreenModel(
                isLoading = false,
                isError = false,
                useBracketsForExpense = false,
                currencySymbol = "$",
                decimalSeparator = ".",
                thousandsSeparator = ",",
            ),
            onNavigateBack = { },
            onUseBracketsChange = { },
            onCurrencyChange = { },
            onDecimalSeparatorChange = { },
            onThousandsSeparatorChange = { },
            onStartTrackingClick = { },
            onSaveClick = {},
        )
    }
}