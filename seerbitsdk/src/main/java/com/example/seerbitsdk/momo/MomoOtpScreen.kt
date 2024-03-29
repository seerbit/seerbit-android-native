package com.example.seerbitsdk.momo


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.OTPInputField
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.otp.MomoOtpDto
import com.example.seerbitsdk.models.query.QueryData
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MOMOOTPScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    linkingReference: String?,
    actionListener: ActionListener?,
) {

    var otp by remember { mutableStateOf("") }
    var myBVN by remember { mutableStateOf("") }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)


// if there is an error loading the report
    if (merchantDetailsState.hasError) {
        ErrorDialog(
            message = merchantDetailsState.errorMessage
                ?: stringResource(R.string.Something_went_wrong)
        )
    }

    if (merchantDetailsState.isLoading) {
        showCircularProgress(showProgress = true)
    }


    merchantDetailsState.data?.let { merchantDetailsData ->

        Column(modifier = modifier) {

            Column(
                modifier = modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(25.dp))


                val amount = merchantDetailsData.payload?.amount
                val fee = calculateTransactionFee(
                    merchantDetailsData,
                    TransactionType.MOMO.type,
                    amount = amount?.toDouble() ?: 0.0
                )
                val totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
                var queryData: QueryData? = null
                var goHome = remember { mutableStateOf(false) }

                SeerbitPaymentDetailHeaderTwo(
                    charges = fee?.toDouble() ?: 0.0,
                    amount = amount ?: "",
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = exitOnSuccess.value,
                    onSuccess = { actionListener?.onSuccess(queryData) }
                ) {
                    openDialog.value = false
                    if (goHome.value) {
                        navController.navigateSingleTopNoSavedState(MOMO.route)
                    }
                }
                val momoOtpDto = MomoOtpDto(linkingReference, otp)

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
                        otpState.errorMessage ?: stringResource(R.string.Something_went_wrong)
                    alertDialogHeaderMessage = stringResource(R.string.failed)
                    goHome.value = true
                    transactionViewModel.resetTransactionState()
                }

                //enter payment states
                if (queryTransactionStateState.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage =
                        queryTransactionStateState.errorMessage
                            ?: stringResource(R.string.Something_went_wrong)
                    alertDialogHeaderMessage = stringResource(R.string.failed)
                    transactionViewModel.resetTransactionState()
                }

                otpState.data?.let {
                    Log.w("startotp", "starting otp")
                    showCircularProgressBar = true
                    if (otpState.data.data?.code == PENDING_CODE) {
                        val paymentReferenceAfterInitiate =
                            it.data?.payments?.paymentReference ?: ""

                        if (queryTransactionStateState.data != null) {
                            Log.w("startotp", "${queryTransactionStateState.data}")

                            when (queryTransactionStateState.data.data?.code) {
                                SUCCESS -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    exitOnSuccess.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.data.data.payments?.reason ?: ""
                                    queryData = queryTransactionStateState.data.data
                                    alertDialogHeaderMessage = stringResource(R.string.success)
                                    return@let
                                }
                                PENDING_CODE -> {
                                    Log.w("startotp", "starting query again")
                                    transactionViewModel.queryTransaction(
                                        paymentReferenceAfterInitiate
                                    )
                                }
                                else -> {
                                    Log.w("startotp", "stop queyr")
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.errorMessage
                                            ?: stringResource(R.string.Something_went_wrong)
                                    alertDialogHeaderMessage = stringResource(R.string.failed)
                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                            }

                        } else {
                            Log.w("startotp", "${queryTransactionStateState.data} on null")

                            transactionViewModel.queryTransaction(paymentReferenceAfterInitiate)
                        }
                    } else if (otpState.data.data?.code == FAILED || otpState.data.data?.code == FAILED_CODE) {
                        openDialog.value = true
                        showCircularProgressBar = false
                        alertDialogMessage =
                            otpState.data.data.message.toString()
                        alertDialogHeaderMessage = stringResource(R.string.failed)
                        goHome.value = true
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
                        text = stringResource(id = R.string.kindly_enter_otp),
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
                OTPInputField(Modifier, stringResource(id = R.string.enter_otp)) {
                    otp = it
                }
                Spacer(modifier = modifier.height(30.dp))
                AuthorizeButton(
                    buttonText = stringResource(id = R.string.authorize_payment),
                    onClick = {
                        keyboardController?.hide()
                        if (otp.isEmpty()) {
                            openDialog.value = true
                            alertDialogMessage = "Error "
                            alertDialogHeaderMessage = "Invalid OTP"
                        } else {
                            transactionViewModel.sendOtp(momoOtpDto)
                        }
                    }, !showCircularProgressBar
                )

                Spacer(modifier = Modifier.height(40.dp))
                OtherPaymentButtonComponent(
                    onOtherPaymentButtonClicked = {
                        navController.navigatePopUpToOtherPaymentScreen(
                            "${Route.OTHER_PAYMENT_SCREEN}/${TransactionType.MOMO.type}"
                        )
                    },
                    onCancelButtonClicked = {
                        navController.navigateSingleTopNoSavedState(
                            Debit_CreditCard.route
                        )
                    },
                    enable = true
                )
                Spacer(modifier = Modifier.height(20.dp))
                BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

