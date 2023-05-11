package com.example.seerbitsdk.card

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.Transaction
import com.example.seerbitsdk.models.query.QueryData
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.CardEnterPinViewModel
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun OTPScreen(
    modifier: Modifier = Modifier,
    otpText: String,
    linkingRef: String,
    merchantDetailsState: MerchantDetailsState,
    cardEnterPinViewModel: CardEnterPinViewModel,
    transactionViewModel: TransactionViewModel,
    navController: NavHostController,
    actionListener: ActionListener?

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


                var goHome = remember { mutableStateOf(false) }
                var showCircularProgressBar by remember { mutableStateOf(false) }
                var otp by remember { mutableStateOf("") }
                var alertDialogMessage by remember { mutableStateOf("") }
                var alertDialogHeaderMessage by remember { mutableStateOf("") }
                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""
                //HANDLE ENTER OTP STATE

                val otpState: OTPState = cardEnterPinViewModel.otpState.value
                val queryTransactionStateState: QueryTransactionState =
                    cardEnterPinViewModel.queryTransactionState.value
                val openDialog = remember { mutableStateOf(false) }
                var amount = merchantDetailsData.payload?.amount
                val exitOnSuccess = remember { mutableStateOf(false) }
                val activity = (LocalContext.current as? Activity)
                var queryData : QueryData? = null

                val fee = calculateTransactionFee(
                    merchantDetailsData,
                    TransactionType.CARD.type,
                    amount = amount?.toDouble() ?: 0.0
                )

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
                    showCircularProgress(true)
                }


                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = exitOnSuccess.value,
                    onSuccess = {actionListener?.onSuccess(queryData)}
                ) {
                    openDialog.value = false
                    showCircularProgressBar = false
                    if(goHome.value){
                        navController.navigateSingleTopNoSavedState(Debit_CreditCard.route)
                    }
                }


                val cardOTPDTO = CardOTPDTO(transaction = Transaction(linkingRef, otp))
                var otpHeaderText = ""
                val alternativeOTPText: String = "Enter OTP"



                otpHeaderText = otpText.ifEmpty {
                    alternativeOTPText
                }


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



                if (otpState.hasError) {
                    showCircularProgressBar = false
                    alertDialogMessage = otpState.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Error"
                    openDialog.value = true
                    goHome.value = true
                    cardEnterPinViewModel.resetTransactionState()
                }

                if (otpState.isLoading) {
                    showCircularProgressBar = true
                }

                otpState.data?.let { otp ->

                    if (otpState.data.status == "SUCCESS") {
                        showCircularProgressBar = false
                        if (queryTransactionStateState.data != null) {

                            if (queryTransactionStateState.data.data?.code == SUCCESS) {
                                showCircularProgressBar = false
                                openDialog.value = true
                                alertDialogMessage = queryTransactionStateState.data.data.payments?.reason?:""
                                queryData = queryTransactionStateState.data.data
                                alertDialogHeaderMessage = "Success"
                                exitOnSuccess.value = true
                                return@let
                            }
                            do {
                                cardEnterPinViewModel.queryTransaction(transactionViewModel.paymentRef.value)
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


                        } else cardEnterPinViewModel.queryTransaction(transactionViewModel.paymentRef.value)
                    } else if (otpState.data.status == FAILED) {
                        showCircularProgressBar = false
                        openDialog.value = true
                        alertDialogMessage = otpState.data.data?.message ?: "Error processing otp"
                        alertDialogHeaderMessage = "Failed"
                        goHome.value = true
                        cardEnterPinViewModel.resetTransactionState()
                    }
                }

                //querying transaction happens after otp has been inputted
                if (queryTransactionStateState.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage = queryTransactionStateState.errorMessage?: "Error processing otp"
                    alertDialogHeaderMessage = "Failed"
                    cardEnterPinViewModel.resetTransactionState()
                }

                if (queryTransactionStateState.isLoading) {
                    showCircularProgressBar = true
                }

                Spacer(modifier = Modifier.height(100.dp))
               if (merchantDetailsData.payload?.tokenize != true && merchantDetailsData.payload?.defaultCurrency !="USD"){
                    OtherPaymentButtonComponent(
                        onOtherPaymentButtonClicked = { navController.navigatePopUpToOtherPaymentScreen("${Route.OTHER_PAYMENT_SCREEN}/${TransactionType.CARD.type}") },
                        onCancelButtonClicked = {navController.navigateSingleTopNoSavedState(
                            Debit_CreditCard.route)},
                        enable = !showCircularProgressBar
                    )}
                else{
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Button(
                            onClick = {  activity?.finish()
                                actionListener?.onClose()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = SignalRed),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .width(160.dp)
                                .height(50.dp)

                        ) {
                            Text(
                                text = "Cancel Payment",

                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Faktpro,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 10.sp,
                                    color = DeepRed,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                            )
                        }

                    }


                }

                Spacer(modifier = Modifier.height(100.dp))
                BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.height(16.dp))





            }

        }


    }
}


//@Preview(showBackground = true, widthDp = 400, heightDp = 700)
//@Composable
//fun OTPScreenPreview() {
//    SeerBitTheme {
//        OTPScreen(
//            onPaymentMethodClick = {},
//            useOtp = true,
//            otpText = "",
//            linkingRef = "",
//            navController = rememberNavController(),
//            merchantDetailsState = null,
//            onOtherPaymentButtonClicked = { /*TODO*/ },
//            cardEnterPinViewModel = ,
//            transactionViewModel =
//        )
//        OTPScreen(
//            onPaymentMethodClick = {}
//        )
//    }
//}

@Composable
fun showCircularProgressTwo(showProgress: Boolean) {
    if(showProgress) {
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
}


@Composable
fun OTPInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterOTP: (String) -> Unit
) {
    Column {
        Card(
            modifier = modifier, elevation = 1.dp,
            border = BorderStroke(0.5.dp, Color.LightGray)
        ) {
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
                    onEnterOTP(newText)
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
fun AuthorizeButton(
    buttonText: String,
    onClick: () -> Unit, enableButton: Boolean
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
        shape = RoundedCornerShape(8.dp),
        enabled = enableButton,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()

    ) {
        Text(
            text = buttonText, color = Color.White,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp
            )
        )
    }

}
