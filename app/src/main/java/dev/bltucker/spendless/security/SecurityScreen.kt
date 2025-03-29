package dev.bltucker.spendless.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import dev.bltucker.spendless.authentication.RE_AUTH_SUCCESS
import dev.bltucker.spendless.common.composables.ErrorScreen
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import kotlinx.serialization.Serializable



@Serializable
data class SecurityScreenNavArgs(
    val userId: Long
)


fun NavGraphBuilder.securityScreen(onNavigateBack: () -> Unit,
                                   onPromptForPin: () -> Unit,) {
    composable<SecurityScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<SecurityScreenNavArgs>()
        val viewModel = hiltViewModel<SecuritySettingsScreenViewModel>()

        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val savedStateHandle = backStackEntry.savedStateHandle

        LaunchedEffect(savedStateHandle) {
            savedStateHandle.getLiveData<Boolean>(RE_AUTH_SUCCESS).observe(backStackEntry) { success ->
                if (success) {
                    savedStateHandle.remove<Boolean>(RE_AUTH_SUCCESS)
                    viewModel.onSaveClick()
                }
            }
        }

        LaunchedEffect(model.shouldReauthenticate) {
            if(model.shouldReauthenticate){
                viewModel.onClearShouldReauthenticate()
                onPromptForPin()
            }
        }


        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.userId)

            onStopOrDispose {  }
        }

        SecurityScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onNavigateBack = onNavigateBack,
            onSessionDurationSelected = viewModel::onSessionDurationChange,
            onLockoutDurationSelected = viewModel::onLockedOutDurationChange,
            onUpdateBiometricsEnabled = viewModel::onBiometricsEnabledChange,
            onSaveClick = viewModel::onSaveClick
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityScreenContent(
    modifier: Modifier = Modifier,
    model: SecuritySettingsModel,
    onNavigateBack: () -> Unit,
    onSessionDurationSelected: (Int) -> Unit,
    onLockoutDurationSelected: (Int) -> Unit,
    onUpdateBiometricsEnabled: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when {
            model.isLoading -> LoadingSpinner(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            model.isError -> ErrorScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            else -> SecuritySettingsColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                model = model,
                onSessionDurationSelected = onSessionDurationSelected,
                onLockoutDurationSelected = onLockoutDurationSelected,
                onUpdateBiometricsEnabled = onUpdateBiometricsEnabled,
                onSaveClick = onSaveClick
            )
        }
    }
}

@Composable
private fun SecuritySettingsColumn(
    modifier: Modifier = Modifier,
    model: SecuritySettingsModel,
    onSessionDurationSelected: (Int) -> Unit,
    onLockoutDurationSelected: (Int) -> Unit,
    onUpdateBiometricsEnabled: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Biometrics for PIN prompt",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(true, false).forEachIndexed { index, enabled ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = 2,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    onClick = { onUpdateBiometricsEnabled(enabled) },
                    selected = model.isBiometricsEnabled == enabled,
                    modifier = Modifier.weight(1f),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFEEE5FF),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    icon = {},
                ) {
                    Text(
                        text = if(enabled) "Enable" else "Disable",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Session expiry duration",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            model.SESSION_DURATION_OPTIONS.forEachIndexed { index, duration ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = model.SESSION_DURATION_OPTIONS.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    onClick = { onSessionDurationSelected(duration) },
                    selected = model.sessionExpirationTimeMinutes == duration,
                    modifier = Modifier.weight(1f),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFEEE5FF),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    icon = {},
                ) {
                    Text(
                        text = when(duration) {
                            5 -> "5 min"
                            15 -> "15 min"
                            30 -> "30 min"
                            60 -> "1 hour"
                            else -> "$duration min"
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Locked out duration",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            model.LOCKOUT_DURATION_OPTIONS.forEachIndexed { index, duration ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = model.LOCKOUT_DURATION_OPTIONS.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    onClick = { onLockoutDurationSelected(duration) },
                    selected = model.lockedOutDurationSeconds == duration,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFEEE5FF),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f),
                    icon = { }
                ) {
                    Text(
                        text = when(duration) {
                            15 -> "15s"
                            30 -> "30s"
                            60 -> "1 min"
                            300 -> "5 min"
                            else -> "${duration}s"
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
    }
}


@Preview(showBackground = true)
@Composable
private fun SecurityScreenContentPreview() {
    SpendLessTheme {
        SecurityScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = SecuritySettingsModel(
                userId = 1L,
                isLoading = false,
                isError = false,
                isBiometricsEnabled = true,
                sessionExpirationTimeMinutes = 15,
                lockedOutDurationSeconds = 30
            ),
            onNavigateBack = { },
            onSessionDurationSelected = { },
            onLockoutDurationSelected = { },
            onUpdateBiometricsEnabled = {},
            onSaveClick = { }
        )
    }
}