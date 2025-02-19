package dev.bltucker.spendless.preferences.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    modifier: Modifier = Modifier,
    currencyList: List<Pair<String,String>>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Currency",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = currencyList.first { it.second == selectedCurrency }.first,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(16.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencyList.forEach { (label, symbol) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onCurrencySelected(symbol)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CurrencyDropDownPreview() {
    val currencies = listOf(
        "$  US Dollar (USD)" to "$",
        "€  Euro (EUR)" to "€",
        "£  British Pound Sterling (GBP)" to "£",
        "¥  Japanese Yen (JPY)" to "¥",
        "CHF  Swiss Franc (CHF)" to "CHF",
        "C$  Canadian Dollar (CAD)" to "C$",
        "A$  Australian Dollar (AUD)" to "A$",
        "¥  Chinese Yuan Renminbi (CNY)" to "¥",
        "₹  Indian Rupee (INR)" to "₹",
        "R  South African Rand (ZAR)" to "R"
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CurrencyDropdown(
                modifier = Modifier.fillMaxWidth(),
                currencyList = currencies,
                selectedCurrency = "$",
                onCurrencySelected = {}
            )

        }
    }
}