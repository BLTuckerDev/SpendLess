package dev.bltucker.spendless.transactions.createtransaction.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bltucker.spendless.common.theme.SpendLessTheme
import kotlin.math.absoluteValue

@Composable
fun MoneyField(
    modifier: Modifier = Modifier,
    amount: Long,
    onAmountChange: (Long) -> Unit,
    isExpense: Boolean,
    currencySymbol: String = "$",
    useBracketsForExpense: Boolean = false,
    decimalSeparator: String = ".",
    thousandsSeparator: String = ","
) {
    val textColor = if (isExpense) Color.Red else Color.Black

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val formattedAmount = formatAmount(amount, decimalSeparator, thousandsSeparator)
        BasicTextField(
            value = formattedAmount,
            onValueChange = { newvalue ->
                onAmountChange(parseAmount(newvalue))
            },
            textStyle = TextStyle(
                fontSize = 32.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {

                    if (useBracketsForExpense) {
                        if(isExpense){
                            Text(
                                text = "(",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else {
                        if (isExpense) {
                            Text(
                                text = "-",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    color = textColor,
                                )
                            )
                        }
                    }

                    Text(
                        text = currencySymbol,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = if (isExpense) Color.Red else Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    if (amount == 0L) {
                        Text(
                            text = "00${decimalSeparator}00",
                            color = Color.Gray,
                        )
                    } else {

                        Text(
                            text = formattedAmount,
                            color = Color.Black,
                        )
                    }


                    if (useBracketsForExpense && isExpense) {
                        Text(
                            text = ")",
                            style = TextStyle(
                                fontSize = 32.sp,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        )
    }
}


private fun formatAmount(
    amount: Long,
    decimalSeparator: String = ".",
    thousandsSeparator: String = ","
): String {

    val dollars = amount.absoluteValue / 100
    val cents = amount.absoluteValue % 100

    val formattedDollars = dollars.toString()
        .reversed()
        .chunked(3)
        .joinToString(thousandsSeparator)
        .reversed()

    val formattedCents = cents.toString().padStart(2, '0')

    val formattedAmount = "$formattedDollars$decimalSeparator$formattedCents"

    return formattedAmount
}

private fun parseAmount(
    formattedAmount: String,
    decimalSeparator: String = ".",
    thousandsSeparator: String = ","
): Long {
    val parts = formattedAmount.split(decimalSeparator)
    if (parts.size != 2) {
        throw IllegalArgumentException("Invalid formatted amount: $formattedAmount")
    }

    val dollarsString = parts[0].replace(thousandsSeparator, "")
    val centsString = parts[1]

    val dollars = dollarsString.toLongOrNull() ?: throw IllegalArgumentException("Invalid dollars part: ${parts[0]}")
    val cents = centsString.toLongOrNull() ?: throw IllegalArgumentException("Invalid cents part: ${parts[1]}")

    if (cents < 0 || cents > 99) {
        throw IllegalArgumentException("Invalid cents value: $cents")
    }

    return dollars * 100 + cents
}

/*
if (isExpense) {
                        if (useBracketsForExpense) {
                            Text(
                                text = "(",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = currencySymbol,
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            innerTextField()
                            if (amount.isNotEmpty()) {
                                Text(
                                    text = ")",
                                    style = TextStyle(
                                        fontSize = 32.sp,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "-$currencySymbol",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            innerTextField()
                        }
                    } else {
                        Text(
                            text = currencySymbol,
                            style = TextStyle(
                                fontSize = 32.sp,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }
 */

@Preview
@Composable
private fun MoneyFieldPreview() {
    SpendLessTheme {
        var text by remember { mutableStateOf("") }
        Surface(
            modifier = Modifier
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MoneyField(
                    amount = 0,
                    onAmountChange = {},
                    isExpense = true,
                    currencySymbol = "$",
                    useBracketsForExpense = false,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 0,
                    onAmountChange = {},
                    isExpense = false,
                    currencySymbol = "$",
                    useBracketsForExpense = false,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 100000,
                    onAmountChange = {},
                    isExpense = true,
                    currencySymbol = "$",
                    useBracketsForExpense = false,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 100000,
                    onAmountChange = {},
                    isExpense = false,
                    currencySymbol = "$",
                    useBracketsForExpense = false,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 0,
                    onAmountChange = {},
                    isExpense = true,
                    currencySymbol = "$",
                    useBracketsForExpense = true,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 0,
                    onAmountChange = {},
                    isExpense = false,
                    currencySymbol = "$",
                    useBracketsForExpense = true,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 100000,
                    onAmountChange = {},
                    isExpense = true,
                    currencySymbol = "$",
                    useBracketsForExpense = true,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )

                MoneyField(
                    amount = 100000,
                    onAmountChange = {},
                    isExpense = false,
                    currencySymbol = "$",
                    useBracketsForExpense = true,
                    decimalSeparator = ".",
                    thousandsSeparator = ","
                )
            }
        }

    }
}