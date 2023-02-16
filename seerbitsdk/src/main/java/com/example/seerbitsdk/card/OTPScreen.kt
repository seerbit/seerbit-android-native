package com.example.seerbitsdk.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.PayViaComponent
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun OTPScreen(
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
                "",
                "",
                ""
            )

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Kindly enter the OTP sent to *******9502 and\n" +
                            "o***********@gmail.com or enter the OTP genrates on your hardware token device",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 14.sp,

                        ),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            OTPInputField(Modifier, "Enter OTP")
            Spacer(modifier = modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Resend OTP")
            }
            Spacer(modifier = modifier.height(10.dp))
            AuthorizeButton(buttonText = "Authorize Payment",
                onClick = { showPinScreen = true }
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
fun OTPScreenPreview() {
    SeerBitTheme {
        OTPScreen(
            onPaymentMethodClick = {}
        )
    }
}


@Composable
fun OTPInputField(modifier: Modifier = Modifier, placeholder: String) {
    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Send
                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}


@Composable
fun AuthorizeButton(
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()

    ) {
        Text(text = buttonText, color = Color.White)
    }

}
