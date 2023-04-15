package com.example.seerbitsdk.card

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.generateSourceIp
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.Transaction
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.CardEnterPinViewModel
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.example.seerbitsdk.viewmodels.MerchantDetailsViewModel


@Composable
fun showCircularProgress(showProgress: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            color = Color.DarkGray,
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun CardEnterPinScreen(
    modifier: Modifier = Modifier,
    onPayButtonClicked: (CardDTO) -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    onOtherPaymentButtonClicked: () -> Unit,
    paymentReference: String,
    cvv: String,
    cardNumber: String,
    cardExpiryMonth: String,
    cardExpiryYear: String,
    isEnterPin: Boolean,
    useOtp: Boolean,
    otpText: String,
    linkingRef: String,
    cardEnterPinViewModel: CardEnterPinViewModel,

    ) {
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
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {


                var pin by remember { mutableStateOf("") }
                var isEnterOTP by remember { mutableStateOf(false) }
                var showCircularProgressBar by remember { mutableStateOf(false) }
                var linkingReference: String? by remember { mutableStateOf("") }
                var paymentReference2 by remember { mutableStateOf("") }
                var otp by remember { mutableStateOf("") }
                var alertDialogMessage by remember { mutableStateOf("") }
                var alertDialogHeaderMessage by remember { mutableStateOf("") }
                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""
                //HANDLE ENTER OTP STATE
                val otpState: OTPState = cardEnterPinViewModel.otpState.value
                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateCardPaymentEnterPinState: InitiateTransactionState =
                    cardEnterPinViewModel.initiateTransactionState.value
                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    cardEnterPinViewModel.queryTransactionState.value
                val openDialog = remember { mutableStateOf(false) }
                var amount = merchantDetailsData.payload?.amount
                val exitOnSuccess = remember { mutableStateOf(false) }
                val activity = (LocalContext.current as? Activity)

                val fee = calculateTransactionFee(
                    merchantDetailsData,
                    TransactionType.CARD.type,
                    amount = amount?.toDouble() ?: 0.0
                )
                var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
                val defaultCurrency = merchantDetailsData.payload?.defaultCurrency ?: ""

                if (isMerchantFeeBearer(merchantDetailsData)) {
                    totalAmount = amount?.toDouble()
                }

                if (useOtp) {
                    linkingReference = linkingRef
                }

                Spacer(modifier = Modifier.height(21.dp))
                SeerbitPaymentDetailHeader(

                    charges = fee?.toDouble() ?: 0.0,
                    amount = amount ?: "",
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    "",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )

                //this handles when to show progress bar
                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = exitOnSuccess.value
                ) {
                    openDialog.value = false
                }



                if (isEnterPin && !isEnterOTP && !useOtp) {
                    Text(
                        text = "Enter your four digit card pin to authorize the payment",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Faktpro,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }

                val cardDTO = CardDTO(
                    deviceType = "Android",
                    country = merchantDetailsData.payload?.country?.countryCode ?: "",
                    amount = totalAmount ?: 0.0,
                    cvv = cvv,
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                    paymentReference = paymentRef,
                    fee = fee,
                    expiryMonth = cardExpiryMonth,
                    fullName = merchantDetailsData.payload?.userFullName,
                    "MASTERCARD",
                    publicKey = merchantDetailsData.payload?.publicKey,
                    expiryYear = cardExpiryYear,
                    source = "MODAL",
                    paymentType = "CARD",
                    sourceIP = generateSourceIp(useIPv4 = true),
                    pin = pin,
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    "LOCAL",
                    false,
                    email = merchantDetailsData.payload?.emailAddress,
                    cardNumber = cardNumber,
                    retry = true
                )

                val cardOTPDTO = CardOTPDTO(transaction = Transaction(linkingReference, otp))
                var otpHeaderText: String = ""
                val alternativeOTPText: String =
                    "Kindly enter the OTP sent to ${merchantDetailsData.payload?.userPhoneNumber?.maskedPhoneNumber()} and\n" +
                            "${merchantDetailsData.payload?.emailAddress} or enter the OTP generates on your hardware token device"

                otpHeaderText = otpText.ifEmpty {
                    alternativeOTPText
                }


                if(initiateCardPaymentEnterPinState.isLoading){
                    showCircularProgressBar = true
                }



                initiateCardPaymentEnterPinState.data?.let {
                    if (it.data?.code == PENDING_CODE) {
                        showCircularProgressBar = false
                        paymentReference2 = it.data.payments?.paymentReference!!
                        linkingReference = it.data.payments.linkingReference
                        isEnterOTP = true
                        otpHeaderText = it.data.message ?:""
                    }

                    if (it.data?.code == "S12") {
                        alertDialogMessage = it.data.message.toString()
                        openDialog.value = true
                        alertDialogHeaderMessage = "Failed"
                    }

                }




                if (isEnterPin && !isEnterOTP && !useOtp) {
                    PinInputField(onEnterPin = { pin = it })
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = otpHeaderText, style = TextStyle(
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
                        otp = it
                    }
                    Spacer(modifier = modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Resend OTP",
                            modifier = Modifier.clickable(enabled = !showCircularProgressBar, "") {

                            })
                    }
                    Spacer(modifier = modifier.height(10.dp))


                    AuthorizeButton(
                        buttonText = "Authorize Payment",
                        onClick = {
                            if (otp.length < 6) {
                                openDialog.value = true
                                alertDialogMessage = "Invalid otp"
                                alertDialogHeaderMessage = "Error Occurred"
                            } else {
                                cardEnterPinViewModel.sendOtp(cardOTPDTO)

                            }
                        }, !showCircularProgressBar
                    )
                }





                if (otpState.hasError) {
                    alertDialogMessage = otpState.errorMessage ?: "Something went wrong"
                    openDialog.value = true
                    alertDialogHeaderMessage = "Error"
                    showCircularProgressBar = false
                    cardEnterPinViewModel.resetTransactionState()
                }
                if( otpState.isLoading){
                    showCircularProgressBar = true
                }

                otpState.data?.let { otp ->

                    if (otpState.data.status == "SUCCESS") {
                        showCircularProgressBar = false
                        if (queryTransactionStateState.data != null) {

                            if (queryTransactionStateState.data.data?.code == SUCCESS) {
                                showCircularProgressBar = false
                                openDialog.value = true
                                alertDialogMessage =
                                    queryTransactionStateState.data.data.payments?.reason!!
                                alertDialogHeaderMessage = "Success"
                                exitOnSuccess.value = true
                                cardEnterPinViewModel.resetTransactionState()
                                return@let
                            }
                            do {
                                cardEnterPinViewModel.queryTransaction(cardDTO.paymentReference!!)
                            } while (queryTransactionStateState.data.data?.code == PENDING_CODE)


                            if (queryTransactionStateState.data.data?.code == FAILED_CODE || queryTransactionStateState.data.data?.code == FAILED) {
                                showCircularProgressBar = false
                                openDialog.value = true
                                alertDialogMessage =
                                    queryTransactionStateState.data.data.payments?.reason ?: ""
                                alertDialogHeaderMessage = "Failed"
                                cardEnterPinViewModel.resetTransactionState()
                                return@let
                            } else {
                                showCircularProgressBar = false
                                openDialog.value = true
                                alertDialogMessage =
                                    queryTransactionStateState.data.data?.payments?.reason ?: ""
                                alertDialogHeaderMessage = "Failed"
                                cardEnterPinViewModel.resetTransactionState()
                                return@let
                            }


                        } else cardEnterPinViewModel.queryTransaction(cardDTO.paymentReference!!)
                    } else if (otpState.data.status == FAILED) {
                        showCircularProgressBar = false
                        openDialog.value = true
                        alertDialogMessage = otpState.data.data?.message ?: "Error processing otp"
                        alertDialogHeaderMessage = "Failed"
                        cardEnterPinViewModel.resetTransactionState()
                    }
                }


                //enter payment states
                if (initiateCardPaymentEnterPinState.hasError) {
                    showCircularProgressBar = false
                    ErrorDialog(
                        message = initiateCardPaymentEnterPinState.errorMessage
                            ?: "Something went wrong"
                    )
                }

                //querying transaction happens after otp has been inputted
                if (queryTransactionStateState.hasError) {
                    showCircularProgressBar = false
                    ErrorDialog(
                        message = queryTransactionStateState.errorMessage
                            ?: "Something went wrong"
                    )
                }
                showCircularProgressBar = queryTransactionStateState.isLoading



                Spacer(modifier = Modifier.height(16.dp))

                //payment button
                if (isEnterPin && !isEnterOTP && !useOtp) {
                    PayButton(
                        amount = "$defaultCurrency ${formatAmount(cardDTO.amount)}",
                        onClick = {
                            if (pin.length == 4) {
                                onPayButtonClicked(cardDTO)
                            } else {
                                openDialog.value = true
                                alertDialogMessage = "Invalid pin"
                                alertDialogHeaderMessage = "Error"
                            }

                        }, !showCircularProgressBar
                    )
                }

            }


        }
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {

    SeerBitTheme {
        val viewModel: MerchantDetailsViewModel = viewModel()
        val viewModel2: TransactionViewModel = viewModel()
        val cardEnterPinViewModel: CardEnterPinViewModel = viewModel()
        CardEnterPinScreen(
            onPayButtonClicked = {},
            currentDestination = null,
            navController = rememberNavController(),
            merchantDetailsState = viewModel.merchantState.value,
            onOtherPaymentButtonClicked = { /*TODO*/ },
            paymentReference = "",
            cvv = "",
            cardEnterPinViewModel = cardEnterPinViewModel,
            cardNumber = "",
            cardExpiryMonth = "",
            cardExpiryYear = "",
            isEnterPin = false,
            useOtp = true,
            otpText = "",
            linkingRef = ""
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




