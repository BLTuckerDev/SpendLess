package dev.bltucker.spendless.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun PinKeypad(
    modifier: Modifier = Modifier,
    onDigitEntered: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..9 step 3).forEach { rowStart ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (rowStart..minOf(rowStart + 2, 9)).forEach { number ->
                    PinButton(
                        text = number.toString(),
                        onClick = { onDigitEntered(number.toString()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            PinButton(
                text = "0",
                onClick = { onDigitEntered("0") },
                modifier = Modifier.weight(1f)
            )
            DeleteButton(
                modifier = Modifier.weight(1f),
                onClick = onDeleteClick
            )
        }
    }
}


@Composable
private fun DeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x4DEADDFF),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(painter = painterResource(R.drawable.backspace), contentDescription = "Delete")
    }
}

@Preview
@Composable
private fun PinKeypadPreview(){
    SpendLessTheme {
        Surface(){
            PinKeypad(modifier = Modifier.fillMaxSize(),
                onDigitEntered = {},
                onDeleteClick = {})
        }
    }
}