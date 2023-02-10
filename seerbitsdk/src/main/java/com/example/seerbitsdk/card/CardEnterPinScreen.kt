package com.example.seerbitsdk.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.*
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.PayViaComponent
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun CardEnterPinScreen(
    modifier: Modifier = Modifier,
    onNavigateToOtpScreen: () -> Unit

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
            Spacer(modifier = Modifier.height(21.dp))
            SeerbitPaymentDetailScreen(
                charges = 0.45,
                amount = "60,000.00",
                currencyText = "NGN",
                ""
            )


            Text(
                text = "Enter your four digit card pin to authorize the payment",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            PinInputField(onNavigateToOtpScreen = onNavigateToOtpScreen)
            Spacer(modifier = modifier.height(20.dp))
            PayButton(
                amount = "NGN 60,000",
                onClick = onNavigateToOtpScreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(60.dp))

            OtherPaymentButtonComponent(
                onOtherPaymentButtonClicked = { /*TODO*/ },
                onCancelButtonClicked = {})

        }

    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    SeerBitTheme {
        CardEnterPinScreen(
            onNavigateToOtpScreen = {}
        )
    }
}


@Preview(showBackground = true, widthDp = 320)
@Composable
fun PinFieldComponentPreview() {
    SeerBitTheme {
        PinInputField(onNavigateToOtpScreen = {})
    }
}

@Composable
fun PinInputField(
    modifier: Modifier = Modifier,
    onNavigateToOtpScreen: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }


    Column {
        Surface(modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .padding(0.dp)) {

            BasicTextField(
                value = pinText,
                onValueChange = {
                    if (it.length <= 4)
                        pinText = it
                    if (it.length == 4) {
                        run { onNavigateToOtpScreen }
                    }

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Send
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { deco ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(4) { index ->
                            val char = when {
                                index >= pinText.length -> ""
                                else -> {
                                    "*"
                                }
                            }
                            Text(
                                text = char,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(60.dp)
                                    .align(alignment = Alignment.CenterVertically)
                                    .border(
                                        1.dp,
                                        Color.LightGray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(2.dp),
                                style = MaterialTheme.typography.h4,
                                color = Color.Black,
                                textAlign = TextAlign.Center

                            )
                        }
                    }
                }
            )
        }
    }

}



