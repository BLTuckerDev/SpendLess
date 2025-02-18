package dev.bltucker.spendless.registration.createpin

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.composables.ErrorBanner
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPinContent(
    modifier: Modifier = Modifier,
    model: CreatePinScreenModel,
    onNavigateBack: () -> Unit,
    onDigitEntered: (String) -> Unit,
    onDeleteDigit: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().systemBarsPadding()
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
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceContainerLowest
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
                        contentDescription = "App Logo"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Repeat Your PIN",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Enter your PIN again",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                PinDots(pinLength = model.confirmationPin.length)

                Spacer(modifier = Modifier.height(32.dp))

                PinKeypad(
                    onDigitEntered = onDigitEntered,
                    onDeleteClick = onDeleteDigit
                )
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
                    message = it
                )
            }
        }
    }
}