package dev.bltucker.spendless.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun PinButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEADDFF),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinButtonPreview(){
    SpendLessTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)){
            PinButton(text = "1", onClick = {})
            PinButton(text = "2", onClick = {})
            PinButton(text = "3", onClick = {})
        }
    }
}
