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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.theme.SpendLessTheme
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
    onSettingsClick: () -> Unit,

) {
    composable<DashboardScreenNavArgs>() { backStackEntry ->
        val args = backStackEntry.toRoute<DashboardScreenNavArgs>()

        BackHandler {
            onNavigateBack()
        }

        val dashboardActions = DashboardActions(
            onSettingsClick = onSettingsClick,
        )

        DashboardScaffold(
            modifier = Modifier.fillMaxSize(),
            dashboardActions = dashboardActions,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    modifier: Modifier = Modifier,
    dashboardActions: DashboardActions,

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
                title = { Text("Username") },
                actions = {
                    IconButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(color = Color(0x1FFFFFFF), shape = RoundedCornerShape(16.dp)),
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
            Text("Dashboard")
        }
    }
}


@Preview
@Composable
fun DashboardScaffoldPreview() {
    SpendLessTheme {
        val actions = DashboardActions(
            onSettingsClick = {}
        )
        DashboardScaffold(modifier = Modifier.fillMaxSize(),
            dashboardActions = actions)
    }
}