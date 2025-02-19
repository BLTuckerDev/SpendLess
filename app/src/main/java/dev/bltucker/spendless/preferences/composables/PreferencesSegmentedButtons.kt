package dev.bltucker.spendless.preferences.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.theme.SpendLessTheme

@Composable
fun ExpenseFormatSelector(
    modifier: Modifier = Modifier,
    useBrackets: Boolean,
    onFormatChange: (Boolean) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Expenses format",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = 0,
                    count = 2,
                    baseShape = RoundedCornerShape(12.dp)
                ),
                onClick = { onFormatChange(false) },
                selected = !useBrackets,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0xFFEEE5FF),
                    inactiveContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f),
                icon = { }
            ) {
                Text("-$10")
            }

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = 1,
                    count = 2,
                    baseShape = RoundedCornerShape(12.dp)
                ),
                onClick = { onFormatChange(true) },
                selected = useBrackets,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0xFFEEE5FF),
                    inactiveContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f),
                icon = { }
            ) {
                Text("($10)")
            }
        }
    }
}

@Composable
fun DecimalSeparatorSelector(
    modifier: Modifier = Modifier,
    selectedSeparator: String,
    onSeparatorSelected: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Decimal separator",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            val separators = listOf(".", ",", " ")
            val examples = listOf("1.00", "1,00", "1 00")

            separators.forEachIndexed { index, separator ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = separators.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    onClick = { onSeparatorSelected(separator) },
                    selected = selectedSeparator == separator,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFEEE5FF),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f),
                    icon = { }
                ) {
                    Text(examples[index])
                }
            }
        }
    }
}

@Composable
fun ThousandsSeparatorSelector(
    modifier: Modifier = Modifier,
    selectedSeparator: String,
    onSeparatorSelected: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Thousands separator",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            val separators = listOf(".", ",", " ")
            val examples = listOf("1.000", "1,000", "1 000")

            separators.forEachIndexed { index, separator ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = separators.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    onClick = { onSeparatorSelected(separator) },
                    selected = selectedSeparator == separator,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFEEE5FF),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f),
                    icon = { }
                ) {
                    Text(examples[index])
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormatSelectorsPreview() {
    SpendLessTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExpenseFormatSelector(
                useBrackets = false,
                onFormatChange = {}
            )

            DecimalSeparatorSelector(
                selectedSeparator = ".",
                onSeparatorSelected = {}
            )

            ThousandsSeparatorSelector(
                selectedSeparator = ",",
                onSeparatorSelected = {}
            )
        }
    }
}