package dev.bltucker.spendless.registration.newuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.composables.ErrorBanner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest

const val NEW_USER_SCREEN_ROUTE = "newUser"

data class NewUserScreenActions(
    val onNavigateBack: () -> Unit,
    val onUsernameChange: (String) -> Unit,
    val onNextClick: () -> Unit,
    val onAlreadyHaveAccountClick: () -> Unit
)

fun NavGraphBuilder.newUserScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePin: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    composable(NEW_USER_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<NewUserScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val actions = NewUserScreenActions(
            onNavigateBack = onNavigateBack,
            onUsernameChange = viewModel::onUsernameChange,
            onNextClick = viewModel::onNextClick,
            onAlreadyHaveAccountClick = onNavigateToLogin
        )

        LaunchedEffect(model.canAdvanceToPinCreation) {
            if(model.canAdvanceToPinCreation){
                onNavigateToCreatePin(model.username)
                viewModel.onHandledNavigation()
            }
        }

        NewUserScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = model,
            actions = actions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUserScreenContent(
    modifier: Modifier = Modifier,
    model: NewUserScreenModel,
    actions: NewUserScreenActions
) {
    val usernameFocusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = SurfaceContainerLowest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = { },
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
                        containerColor = dev.bltucker.spendless.common.theme.Surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                )

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(color = Primary, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wallet_money),
                        contentDescription = "App Logo",
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome to SpendLess!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "How can we address you?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Create a unique username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = model.username,
                    onValueChange = actions.onUsernameChange,
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(usernameFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (model.isUsernameValid()) {
                                actions.onNextClick()
                            }
                        }
                    ),
                    isError = model.errorMessage != null
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = actions.onNextClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = model.isUsernameValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                TextButton(
                    onClick = actions.onAlreadyHaveAccountClick,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = model.errorMessage != null,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            model.errorMessage?.let {
                ErrorBanner(
                    modifier = Modifier.imePadding(),
                    message = model.errorMessage
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewUserScreenContentPreview() {
    SpendLessTheme {
        NewUserScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = NewUserScreenModel(
                username = "",
                errorMessage = null,
            ),
            actions = NewUserScreenActions(
                onNavigateBack = { },
                onUsernameChange = { },
                onNextClick = { },
                onAlreadyHaveAccountClick = { }
            )
        )
    }
}
