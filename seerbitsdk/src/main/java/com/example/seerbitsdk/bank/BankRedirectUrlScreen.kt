package com.example.seerbitsdk.bank


import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.redirectUrl
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.TransactionViewModel

@Composable
fun BankRedirectUrlScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel,
    merchantDetailsState: MerchantDetailsState?,
    bankCode: String,
    bankName: String
) {


    var json: String = ""
    var showErrorDialog by remember { mutableStateOf(false) }

    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var redirectUrl = ""


    // if there is an error loading the report
    if (merchantDetailsState?.hasError == true) {
        ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
    }

    if (merchantDetailsState?.isLoading == true) {
        showCircularProgress(showProgress = true)
    }


    merchantDetailsState?.data?.let { merchantDetailsData ->

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
                var amount: String = merchantDetailsData.payload?.amount ?: ""
                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""


                SeerbitPaymentDetailHeaderTwo(
                    charges = merchantDetailsData.payload?.vatFee?.toDouble() ?: 0.0,
                    amount =amount,
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    merchantDetailsData.payload?.businessName ?: "",
                    merchantDetailsData.payload?.supportEmail ?: ""
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Kindly click the button below to authenticate with your bank",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center

                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(10.dp)
                )



                val bankAccountDTO = BankAccountDTO(
                    deviceType = "Android",
                    country = merchantDetailsData.payload?.country?.countryCode ?: "",
                    bankCode = bankCode,
                    amount = amount,
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                    paymentReference = paymentRef,
                    fee = merchantDetailsData.payload?.vatFee,
                    fullName =  merchantDetailsData.payload?.userFullName,
                    channelType = bankName,
                    dateOfBirth = "",
                    publicKey =  merchantDetailsData.payload?.publicKey,
                    source = "",
                    accountName =  merchantDetailsData.payload?.userFullName,
                    paymentType = "ACCOUNT",
                    sourceIP = "128.0.0.1",
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    bvn = "",
                    email =  merchantDetailsData.payload?.emailAddress,
                    productDescription = "",
                    scheduleId = "",
                    accountNumber = "",
                    retry = transactionViewModel.retry.value
                )

                Spacer(modifier = Modifier.height(10.dp))


                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }

                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value
                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateBankAccountPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState.value
                //enter payment states

                showCircularProgressBar = initiateBankAccountPayment.isLoading

                //enter payment states
                if (initiateBankAccountPayment.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage =
                        initiateBankAccountPayment.errorMessage ?: "Something went wrong"
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

                initiateBankAccountPayment.data?.let {
                    showCircularProgressBar = true
                    if (initiateBankAccountPayment.data.data?.code == PENDING_CODE) {
                        val paymentReferenceAfterInitiate =
                            it.data?.payments?.paymentReference ?: ""
                        redirectUrl = it.data?.payments?.redirectUrl ?: ""
                        redirectUrl(redirectUrl = redirectUrl)
                        transactionViewModel.setRetry(true)
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
                    } else {
                        openDialog.value = true
                        showCircularProgressBar = false
                        alertDialogMessage =
                            initiateBankAccountPayment.data.data?.message.toString()
                        alertDialogHeaderMessage = "Failed"
                        transactionViewModel.resetTransactionState()
                        return@let
                    }
                }




                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Authorize Payment",
                    onClick = {
                        if (bankCode.isNotEmpty()) {

                            transactionViewModel.initiateTransaction(bankAccountDTO)
                        } else {
                            showErrorDialog = true
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
