package com.example.seerbitsdk.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.InitiateTransactionViewModel
import com.example.seerbitsdk.viewmodels.MerchantDetailsViewModel
import kotlinx.coroutines.launch


@Composable
fun showCircularProgress(showProgress: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            color = Color.DarkGray,
        )
    }
}

@Composable
fun CardEnterPinScreen(
    modifier: Modifier = Modifier,
    onPayButtonClicked: (CardDTO) -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    onOtherPaymentButtonClicked: () -> Unit,
    initiateTransactionViewModel: InitiateTransactionViewModel,
    paymentReference: String,
    cvv: String,
    cardNumber: String,
    cardExpiryMonth: String,
    cardExpiryYear: String

) {
    var showPinScreen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {


        // if there is an error loading the report
        if (merchantDetailsState.hasError) {
            ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
        }

        if (merchantDetailsState.isLoading) {
            showCircularProgress(showProgress = true)
        }


        merchantDetailsState.data?.let { merchantDetailsData ->

            Column(
                modifier = modifier
                    .padding(start = 21.dp, end = 21.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                var pin by rememberSaveable { mutableStateOf("") }
                var showErrorDialog by rememberSaveable { mutableStateOf(false) }
                var showCircularProgressBar by rememberSaveable { mutableStateOf(false) }

                Spacer(modifier = Modifier.height(21.dp))
                SeerbitPaymentDetailScreen(
                    charges = 0.45,
                    amount = "60,000.00",
                    currencyText = "NGN",
                    "",
                    merchantDetailsData.payload?.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
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
                Spacer(modifier = Modifier.height(40.dp))


                if (showErrorDialog) {
                    ErrorDialog(message = "invalid pin")
                }

                val cardDTO = CardDTO(
                    deviceType = "Desktop",
                    country = merchantDetailsData.payload?.address?.country!!,
                    60000.0,
                    cvv = cvv,
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload.number,
                    paymentReference = initiateTransactionViewModel.generateRandomReference(),
                    fee = merchantDetailsData.payload.cardFee?.mc,
                    expiryMonth = cardExpiryMonth,
                    fullName = "Amos Aorme",
                    "MASTERCARD",
                    publicKey = merchantDetailsData.payload.testPublicKey,
                    expiryYear = cardExpiryYear,
                    source = "",
                    paymentType = "CARD",
                    sourceIP = "0.0.0.1",
                    pin = "",
                    currency = merchantDetailsData.payload.defaultCurrency,
                    "LOCAL",
                    false,
                    email = "inspiron.amos@gmail.com",
                    cardNumber = cardNumber,
                    retry = false
                )
                cardDTO.paymentReference = initiateTransactionViewModel.generateRandomReference()

                PinInputField(onEnterPin = {
                    pin = it
                })


                //HANDLE INITIATE TRANSACTION RESPONSE
                val transactionState: InitiateTransactionState =
                    initiateTransactionViewModel.initiateTransactionState.value

                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    initiateTransactionViewModel.queryTransactionState.value

                if (transactionState.hasError) {
                    ErrorDialog(message = transactionState.errorMessage ?: "Something went wrong")

                }

                if (transactionState.isLoading) {
                    showCircularProgress(showProgress = true)
                }

                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }


                transactionState.data?.let {
                    val paymentReference2 = it.data?.payments?.paymentReference

                    if (paymentReference2 != null)
                        initiateTransactionViewModel.queryTransaction(paymentReference2)
                    else {
                        ErrorDialog(message = it.message ?: "An Error Occurred")
                    }

                }


                if (queryTransactionStateState.hasError) {
                    ErrorDialog(
                        message = queryTransactionStateState.errorMessage ?: "Something went wrong"
                    )
                }

                if (queryTransactionStateState.isLoading) {
                    showCircularProgressBar = true
                }

                if (queryTransactionStateState.data?.data != null && transactionState.data?.data?.payments?.paymentReference != null) {
                    if (queryTransactionStateState.data.data.code != PENDING_CODE) {
                        ErrorDialog(message = "Success!!")
                        showCircularProgressBar = false
                    } else {
                        showCircularProgressBar = true
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                //payment button
                PayButton(
                    amount = "NGN 60,000",
                    onClick = {

                        showErrorDialog = if (pin.length < 4) {
                            true
                        } else {
                            onPayButtonClicked(cardDTO)
                            false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(76.dp))
                OtherPaymentButtonComponent(
                    onOtherPaymentButtonClicked = onOtherPaymentButtonClicked,
                    onCancelButtonClicked = {})

            }

        }
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {

    SeerBitTheme {
        val viewModel: MerchantDetailsViewModel = viewModel()
        val viewModel2: InitiateTransactionViewModel = viewModel()
        CardEnterPinScreen(
            onPayButtonClicked = {},
            currentDestination = null,
            navController = rememberNavController(),
            merchantDetailsState = viewModel.merchantState.value,
            onOtherPaymentButtonClicked = { /*TODO*/ },
            initiateTransactionViewModel = viewModel2,
            paymentReference = "",
            cvv = "",
            cardNumber = "",
            cardExpiryMonth = "",
            cardExpiryYear = ""
        )
    }
}


@Preview(showBackground = true, widthDp = 320)
@Composable
fun PinFieldComponentPreview() {
    SeerBitTheme {
        PinInputField(onEnterPin = {})
    }
}

@Composable
fun PinInputField(
    modifier: Modifier = Modifier,
    onEnterPin: (String) -> Unit
) {
    var pinText by remember { mutableStateOf("") }


    Column {
        Surface(
            modifier = modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .padding(0.dp)
        ) {

            BasicTextField(
                value = pinText,
                onValueChange = {
                    if (it.length <= 4)
                        pinText = it
                    onEnterPin(pinText)

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




