package com.example.seerbitsdk.bank

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.R

import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.OTPInputField
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun BankAccountNumberScreen(
    modifier: Modifier = Modifier,
    onPaymentMethodClick: (String) -> Unit,
    onConfirmedButtonClicked: (CardDTO) -> Unit,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankAccountNumber: String,
    birthday: String,
    bvn: String,
    otp: String,

) {
    var showPinScreen by remember { mutableStateOf(false) }
    var showBankAccountField by remember { mutableStateOf(false) }
    var showBirthdayField by remember { mutableStateOf(true) }
    var showBVNField by remember { mutableStateOf(false) }
    var showOTPScreen by remember { mutableStateOf(false) }
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
                "Enter your Bank Account Number",
                "",
                ""
            )

            //show
            if (showBankAccountField) {
                Spacer(modifier = Modifier.height(21.dp))
                BankAccountNumberField(Modifier, "10 Digit Bank Account Number") {
                }

                Spacer(modifier = modifier.height(20.dp))

                Spacer(modifier = modifier.height(10.dp))
                AuthorizeButton(buttonText = "Pay $50",
                    onClick = { showPinScreen = true }
                )

            }

            //show bvn
            if (showBVNField) {
                BVNInputField(Modifier, "Enter your BVN Number") {

                }
                Spacer(modifier = modifier.height(20.dp))

                Spacer(modifier = modifier.height(10.dp))
                AuthorizeButton(buttonText = "Pay $50",
                    onClick = { showPinScreen = true }
                )
            }
            //Show Birthday
            if (showBirthdayField) {
                BirthDayInputField(Modifier, "DD / MM / YYYY ") {
                }
                Spacer(modifier = modifier.height(20.dp))

                Spacer(modifier = modifier.height(10.dp))
                AuthorizeButton(buttonText = "Pay $50",
                    onClick = { showPinScreen = true }
                )
            }

            if (showOTPScreen) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kindly enter the OTP sent to *******9502 and\n" +
                                "o***********@gmail.com or enter the OTP generates on your hardware token device",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center

                        ),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                OTPInputField(Modifier, "Enter OTP") {
                    //otp = it
                }
                Spacer(modifier = modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Resend OTP")
                }
                Spacer(modifier = modifier.height(10.dp))

                AuthorizeButton(buttonText = "Authorize Payment",
                    onClick = {
                        //if (otp.length < 6) {
                        //   showErrorDialog = true
                        //} else {
                        //   transactionViewModel.sendOtp(cardOTPDTO)
                        //   showErrorDialog = false
                        // }
                    }
                )
            }
        }


    }

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun BankAccountNumberScreenPreview() {
    val viewModel : TransactionViewModel by viewModel()
    SeerBitTheme {
        BankAccountNumberScreen(
            onPaymentMethodClick = {},
            onConfirmedButtonClicked = {},
            navController = rememberNavController(),
            merchantDetailsState = MerchantDetailsState(),
            transactionViewModel = viewModel,
            bankAccountNumber = "",
            birthday = "",
            bvn = "",
            otp = ""
        )
    }
}


@Composable
fun BirthDayInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBirthday: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.length <= 8)
                        value = newText
                    onEnterBirthday(newText)
                },

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
fun BVNInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBVN: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.length <= 10)
                        value = newText
                    onEnterBVN(newText)
                },

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
fun BankAccountNumberField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBVN: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.length <= 10)
                        value = newText
                    onEnterBVN(newText)
                },

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



