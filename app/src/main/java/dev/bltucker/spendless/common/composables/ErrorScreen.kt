package dev.bltucker.spendless.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.Surface

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        Text(text = "Something has gone wrong",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "We're so sorry",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall)
    }
}


@Preview
@Composable
private fun ErrorScreenPreview(){
    SpendLessTheme {
        Surface{
            ErrorScreen(modifier = Modifier.fillMaxSize())
        }
    }
}