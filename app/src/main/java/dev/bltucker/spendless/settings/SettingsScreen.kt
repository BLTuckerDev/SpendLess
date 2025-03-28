package dev.bltucker.spendless.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.Surface
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest
import kotlinx.serialization.Serializable


@Serializable
data class SettingsScreenNavArgs(
    val userId: Long
)


data class SettingsScreenActions(
    val onNavigateBack: () -> Unit,
    val onPreferencesClick: () -> Unit,
    val onSecurityClick: () -> Unit,
    val onLogoutClick: () -> Unit
)


fun NavGraphBuilder.settingsScreen(onNavigateBack: () -> Unit,
                                   onNavigateToPreferences: (Long) -> Unit,
                                   onNavigateToSecurity: (Long) -> Unit,
                                   onNavigateToLogout: () -> Unit) {
    composable<SettingsScreenNavArgs>() { backStackEntry ->
        val args = backStackEntry.toRoute<SettingsScreenNavArgs>()
        val viewModel = hiltViewModel<SettingsScreenViewmodel>()
        val actions = SettingsScreenActions(
            onNavigateBack = onNavigateBack,
            onPreferencesClick = { onNavigateToPreferences(args.userId)},
            onSecurityClick = { onNavigateToSecurity(args.userId)},
            onLogoutClick = {
                viewModel.onLogout()
                onNavigateToLogout()
            }
        )

        SettingsScreenContent(
            modifier = Modifier.fillMaxSize(),
            actions = actions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    actions: SettingsScreenActions
) {
    Scaffold(
        modifier = modifier,
        containerColor = SurfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = actions.onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            PreferencesAndSecurityCard(
                onPreferencesClick = actions.onPreferencesClick,
                onSecurityClick = actions.onSecurityClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            LogoutButton(
                onClick = actions.onLogoutClick
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SpendLessTheme {
        val mockActions = SettingsScreenActions(
            onNavigateBack = {},
            onPreferencesClick = {},
            onSecurityClick = {},
            onLogoutClick = {}
        )

        SettingsScreenContent(
            modifier = Modifier.fillMaxSize(),
            actions = mockActions
        )
    }
}