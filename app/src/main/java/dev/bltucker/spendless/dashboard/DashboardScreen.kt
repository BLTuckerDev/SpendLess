package dev.bltucker.spendless.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.composables.ErrorScreen
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.room.SpendLessUser
import dev.bltucker.spendless.common.room.UserPreferences
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.dashboard.composables.AccountBalance
import kotlinx.serialization.Serializable

@Serializable
data class DashboardScreenNavArgs(
    val userId: Long,
)


data class DashboardActions(
    val onSettingsClick: () -> Unit,
)

fun NavGraphBuilder.dashboardScreen(
    onNavigateBack: () -> Unit,
    onSettingsClick: (Long) -> Unit,

) {
    composable<DashboardScreenNavArgs>() { backStackEntry ->
        val args = backStackEntry.toRoute<DashboardScreenNavArgs>()
        val viewModel = hiltViewModel<DashboardScreenViewModel>()

        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.userId)

            onStopOrDispose {  }
        }

        BackHandler {
            onNavigateBack()
        }

        val dashboardActions = DashboardActions(
            onSettingsClick = { onSettingsClick(args.userId)},
        )

        when{
            model.isLoading -> LoadingSpinner()
            model.isError -> ErrorScreen()
            else -> {
                DashboardScaffold(
                    modifier = Modifier.fillMaxSize(),
                    dashboardActions = dashboardActions,
                    model = model,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    modifier: Modifier = Modifier,
    dashboardActions: DashboardActions,
    model: DashboardScreenModel,

){

    BottomSheetScaffold(
        modifier = modifier,
        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Latest Transactions",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = { /* Handle show all */ }) {
                        Text("Show all")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        sheetPeekHeight = 400.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        topBar = {
            TopAppBar(
                title = { Text(
                    text = model.user?.username ?: "",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge)
                },
                actions = {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                color = Color(0x1FFFFFFF),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        onClick = { dashboardActions.onSettingsClick() }) {
                        Icon(
                            painter = painterResource(R.drawable.export),
                            contentDescription = "Export",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(
                                color = Color(0x1FFFFFFF),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        onClick = { dashboardActions.onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {

            AccountBalance(
                modifier = modifier.fillMaxWidth().padding(vertical = 40.dp ,horizontal = 72.dp),
                accountBalance = model.formattedAccountBalance()
            )

        }
    }
}


@Preview
@Composable
fun DashboardScaffoldPreview() {
    SpendLessTheme {

        val model = DashboardScreenModel(
            user = SpendLessUser(username = "test", pinHash = "", pinSalt = ""),
            userPreferences = UserPreferences(0, false, "$", ".", ","),
            isLoading = false,
            isError = false
        )

        val actions = DashboardActions(
            onSettingsClick = {}
        )
        DashboardScaffold(modifier = Modifier.fillMaxSize(),
            dashboardActions = actions,
            model = model)
    }
}