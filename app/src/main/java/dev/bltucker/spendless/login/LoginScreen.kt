package dev.bltucker.spendless.login

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

const val LOGIN_SCREEN_ROUTE = "login"

data class LoginScreenActions(
    val onLoginClick: () -> Unit,
    val onNewUserClick: () -> Unit,
    val onUsernameChange: (String) -> Unit,
    val onPinChange: (String) -> Unit,
)

fun NavGraphBuilder.loginScreen(
    onNavigateToNewUser: () -> Unit,
    onLoginSuccess: (Long) -> Unit
) {
    composable(route = LOGIN_SCREEN_ROUTE){
        val viewModel = hiltViewModel<LoginViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val actions = LoginScreenActions(
            onLoginClick = viewModel::onLoginClick,
            onNewUserClick = onNavigateToNewUser,
            onUsernameChange = viewModel::onUsernameChange,
            onPinChange = viewModel::onPinChange
        )


        LaunchedEffect(model.loginSuccessful) {
            if(model.loginSuccessful){
                val userId = model.loggedInUserId
                userId?.let{
                    onLoginSuccess(userId)
                }
                viewModel.handledLoginSuccessful()
            }
        }

        LoginScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = model,
            actions = actions,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    model: LoginScreenModel,
    actions: LoginScreenActions
) {
    val pinFocusRequester = remember { FocusRequester() }

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
                Spacer(modifier = Modifier.height(72.dp))

                Box(modifier = Modifier.size(64.dp).background(color = Primary, shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.wallet_money),
                        contentDescription = "App Logo",
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Enter you login details",
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
                        .focusRequester(FocusRequester()),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { pinFocusRequester.requestFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = model.pin,
                    onValueChange = {
                        if (it.length <= 5) {
                            actions.onPinChange(it)
                        }
                    },
                    label = { Text("PIN") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(pinFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (model.username.isNotBlank() && model.pin.length == 5) {
                                actions.onLoginClick()
                            }
                        }
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { actions.onLoginClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = model.username.isNotBlank() && model.pin.length == 5,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                TextButton(
                    onClick = actions.onNewUserClick,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "New to SpendLess?",
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
            model.errorMessage?.let { error ->
                ErrorBanner(
                    message = error,
                    modifier = Modifier.imePadding()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SpendLessTheme {
        LoginScreenContent(
            modifier = Modifier.fillMaxSize(),
            model = LoginScreenModel(
                username = "",
                pin = "",
                loginSuccessful = false
            ),
            actions = LoginScreenActions(
                onLoginClick = { },
                onNewUserClick = {},
                onUsernameChange = {},
                onPinChange = {}
            )
        )
    }
}
