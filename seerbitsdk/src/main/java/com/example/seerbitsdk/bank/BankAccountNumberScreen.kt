package com.example.seerbitsdk.bank

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.PayViaComponent
import com.example.seerbitsdk.BottomSeerBitWaterMark
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.OTPInputField
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun BankAccountNumberScreen(
    modifier: Modifier = Modifier,
    onPaymentMethodClick: (String) -> Unit
) {
    var showPinScreen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {

        Column(
            modifier = modifier
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            SeerbitPaymentDetailScreen(
                charges = 0.45,
                amount = "60,000.00",
                currencyText = "NGN",
                "Enter your Bank Account Number"
            )

            Spacer(modifier = Modifier.height(21.dp))

            OTPInputField(Modifier,"10 Digit Bank Account Number")
            Spacer(modifier = modifier.height(20.dp))

            Spacer(modifier = modifier.height(10.dp))
            AuthorizeButton(buttonText = "Pay $50",
                onClick = { showPinScreen = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PayViaComponent()
            Spacer(modifier = Modifier.height(8.dp))

        }

    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun BankAccountNumberScreenPreview() {
    SeerBitTheme {
        BankAccountNumberScreen(onPaymentMethodClick = {})
    }
}