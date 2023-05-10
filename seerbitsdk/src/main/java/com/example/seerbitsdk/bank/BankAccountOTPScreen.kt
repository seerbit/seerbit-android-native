package com.example.seerbitsdk.bank

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.seerbitsdk.*
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.OTPInputField
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.otp.BankAccountOtpDto
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.navigatePopUpToOtherPaymentScreen
import com.example.seerbitsdk.navigateSingleTopTo
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.query.QueryData
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BankAccountOTPScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankAccountNumber: String,
    dob: String,
    bvn: String,
    bankCode: String?,
    requiredFields: RequiredFields?,
    bankName: String?,
    linkingReference: String?,
    actionListener: ActionListener?,

    ) {

    var otp by remember { mutableStateOf("") }
    var myBVN by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    myBVN = if (bvn == Dummy) "" else bvn
    dateOfBirth = if (dob == Dummy) "" else dob
    val keyboardController = LocalSoftwareKeyboardController.current
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val goHome = remember { mutableStateOf(false) }


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
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                val email   =   merchantDetailsData.payload?.emailAddress
                val maskedPhoneNumber = merchantDetailsData.payload?.userPhoneNumber?.maskedPhoneNumber()
                val filteredEmailIndex=email?.indexOf("@")
                val maskedEmailAddress = "${email?.get(0)}*************${email?.substring(
                    filteredEmailIndex?.minus(2) ?: 0
                )}"
                var amount = merchantDetailsData.payload?.amount
                val currency = merchantDetailsData.payload?.defaultCurrency?:""
                val fee =  calculateTransactionFee(merchantDetailsData, TransactionType.ACCOUNT.type, amount = amount?.toDouble()?:0.0)
                val totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
                var queryData : QueryData? = null


                SeerbitPaymentDetailHeaderTwo(
                    charges =  fee?.toDouble()?:0.0,
                    amount = amount?:"",
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = exitOnSuccess.value,
                    onSuccess = {actionListener?.onSuccess(queryData)}
                ) {
                    openDialog.value = false
                    if(goHome.value){
                        navController.navigateSingleTopNoSavedState(BankAccount.route)
                    }
                }

                val bankAccountOtpDtO = BankAccountOtpDto(linkingReference, otp)

                 //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value

                //HANDLE INITIATE TRANSACTION RESPONSE
                val otpState : OTPState =
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
                    goHome.value = true
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
                    if (otpState.data.data?.code== SUCCESS) {
                        val paymentReferenceAfterInitiate = it.data?.payments?.paymentReference?:""

                        if (queryTransactionStateState.data != null) {

                            when (queryTransactionStateState.data.data?.code) {
                                SUCCESS -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    exitOnSuccess.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.data.data.payments?.reason!!
                                    alertDialogHeaderMessage = "Success!!"
                                    queryData = queryTransactionStateState.data.data
                                    return@let
                                }
                                PENDING_CODE -> {
                                    transactionViewModel.queryTransaction(paymentReferenceAfterInitiate)
                                }
                                else -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.errorMessage?: "Something went wrong"
                                    alertDialogHeaderMessage = "Failed"

                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                            }

                        } else transactionViewModel.queryTransaction(paymentReferenceAfterInitiate)
                    } else if (otpState.data.data?.code== FAILED || otpState.data.data?.code == FAILED_CODE) {
                        openDialog.value = true
                        showCircularProgressBar = false
                        alertDialogMessage =
                            otpState.data.data.message.toString()
                        alertDialogHeaderMessage = "Failed"
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
                        text = "Kindly enter the OTP sent to $maskedPhoneNumber and\n" +
                                "$maskedEmailAddress or enter the OTP generates on your hardware token device",
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
                            transactionViewModel.sendOtp(bankAccountOtpDtO)

                        }
                    }, !showCircularProgressBar
                )


                Spacer(modifier = Modifier.height(100.dp))

                OtherPaymentButtonComponent(
                    onOtherPaymentButtonClicked = { navController.navigatePopUpToOtherPaymentScreen("${Route.OTHER_PAYMENT_SCREEN}/${TransactionType.ACCOUNT.type}") },
                    onCancelButtonClicked = {navController.navigateSingleTopNoSavedState(Debit_CreditCard.route)},
                    enable = !showCircularProgressBar
                )

                Spacer(modifier = Modifier.height(100.dp))
                BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.height(16.dp))

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

