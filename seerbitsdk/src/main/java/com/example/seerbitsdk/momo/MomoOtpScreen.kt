package com.example.seerbitsdk.momo


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.OTPInputField
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.otp.MomoOtpDto
import com.example.seerbitsdk.models.otp.Transaction
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MOMOOTPScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    linkingReference: String?,
) {

    var otp by remember { mutableStateOf("") }
    var myBVN by remember { mutableStateOf("") }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var amount: String = "20.00"
    val keyboardController = LocalSoftwareKeyboardController.current


// if there is an error loading the report
    if (merchantDetailsState?.hasError!!) {
        ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
    }

    if (merchantDetailsState.isLoading) {
        showCircularProgress(showProgress = true)
    }




    merchantDetailsState.data?.let { merchantDetailsData ->


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



                SeerbitPaymentDetailHeaderTwo(
                    charges = merchantDetailsData.payload?.vatFee?.toDouble()!!,
                    amount = amount,
                    currencyText = merchantDetailsData.payload.defaultCurrency ?: "",
                    merchantDetailsData.payload.businessName ?: "",
                    merchantDetailsData.payload.supportEmail ?: ""
                )

                val momoOtpDto = MomoOtpDto(transaction = Transaction(linkingReference, otp))

                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value

                //HANDLE INITIATE TRANSACTION RESPONSE
                val otpState: OTPState =
                    transactionViewModel.otpState.value
                //enter payment states


                showCircularProgressBar = otpState.isLoading

                //enter payment states
                if (otpState.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage =
                        otpState.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Failed"
                    transactionViewModel.resetTransactionState()
                }

                //enter payment states
                if (queryTransactionStateState.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage =
                        queryTransactionStateState.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Failed"
                    transactionViewModel.resetTransactionState()
                }

                otpState.data?.let {
                    showCircularProgressBar = true
                    if (otpState.data.data?.code == SUCCESS) {
                        val paymentReferenceAfterInitiate =
                            it.data?.payments?.paymentReference ?: ""

                        if (queryTransactionStateState.data != null) {

                            when (queryTransactionStateState.data.data?.code) {
                                SUCCESS -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.data.data.payments?.reason!!
                                    alertDialogHeaderMessage = "Success"
                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                                PENDING_CODE -> {
                                    transactionViewModel.queryTransaction(
                                        paymentReferenceAfterInitiate
                                    )
                                }
                                else -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.errorMessage
                                            ?: "Something went wrong"
                                    alertDialogHeaderMessage = "Failed"
                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                            }

                        } else transactionViewModel.queryTransaction(paymentReferenceAfterInitiate)
                    } else if (otpState.data.data?.code == FAILED || otpState.data.data?.code == FAILED_CODE) {
                        openDialog.value = true
                        showCircularProgressBar = false
                        alertDialogMessage =
                            otpState.data.data.message.toString()
                        alertDialogHeaderMessage = "Failed"
                        transactionViewModel.resetTransactionState()
                        return@let
                    }
                }



                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kindly enter the OTP sent to your mobile number or email",
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
                    otp = it
                }

                Spacer(modifier = modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Resend OTP")
                }
                Spacer(modifier = modifier.height(10.dp))

                AuthorizeButton(
                    buttonText = "Authorize Payment",
                    onClick = {
                        keyboardController?.hide()
                        if (otp.length < 6) {
                            openDialog.value = true
                            alertDialogMessage = "Error "
                            alertDialogHeaderMessage = "Invalid OTP"
                        } else {
                            transactionViewModel.sendOtp(momoOtpDto)

                        }
                    }, !showCircularProgressBar
                )


                //The alert dialog occurs here
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = alertDialogHeaderMessage, textAlign = TextAlign.Center)
                            }

                        },
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(alertDialogMessage, textAlign = TextAlign.Center)
                            }
                        },
                        confirmButton = {
                            Button(

                                onClick = {
                                    openDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = SignalRed
                                )
                            ) {
                                Text(text = "Close")

                            }
                        },
                    )
                }
            }


        }


    }

}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun OTPScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

    }
}
