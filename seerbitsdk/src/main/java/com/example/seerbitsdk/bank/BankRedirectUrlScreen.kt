package com.example.seerbitsdk.bank


import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.generateSourceIp
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.query.QueryData
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.TransactionViewModel

@Composable
fun BankRedirectUrlScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel,
    merchantDetailsState: MerchantDetailsState?,
    bankCode: String,
    bankName: String,
    navController:NavHostController,
    actionListener: ActionListener?,
) {


    var json: String = ""
    var showErrorDialog by remember { mutableStateOf(false) }

    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var redirectUrl = ""
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val query = remember { mutableStateOf(true) }
    val goHome = remember { mutableStateOf(false) }


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
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""
                var amount = merchantDetailsData.payload?.amount
                val currency = merchantDetailsData.payload?.defaultCurrency ?: ""
                val fee = calculateTransactionFee(
                    merchantDetailsData,
                    TransactionType.ACCOUNT.type,
                    amount = amount?.toDouble() ?: 0.0
                )
                var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
                var queryData : QueryData? = null


                if (isMerchantFeeBearer(merchantDetailsData)) {
                    totalAmount = amount?.toDouble()
                }

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
                    onSuccess = {actionListener?.onSuccess(queryData)}
                ) {
                    openDialog.value = false
                    if(goHome.value){
                        navController.navigateSingleTopNoSavedState(BankAccount.route)
                    }
                }

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
                    amount = totalAmount.toString(),
                    redirectUrl = "http://localhost:3002/#/",
                    productId = merchantDetailsData.payload?.productId,
                    mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                    paymentReference = paymentRef,
                    fee = fee,
                    fullName = merchantDetailsData.payload?.userFullName,
                    channelType = bankName,
                    dateOfBirth = "",
                    publicKey = merchantDetailsData.payload?.publicKey,
                    source = "",
                    accountName = merchantDetailsData.payload?.userFullName,
                    paymentType = "ACCOUNT",
                    sourceIP = generateSourceIp(true),
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    bvn = "",
                    email = merchantDetailsData.payload?.emailAddress,
                    productDescription = merchantDetailsData.payload?.productDescription,
                    scheduleId = "",
                    accountNumber = "",
                    retry = transactionViewModel.retry.value,
                    pocketReference =merchantDetailsData.payload?.pocketReference,
                    vendorId = merchantDetailsData.payload?.vendorId
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

                queryTransactionStateState.data?.let {

                    when (queryTransactionStateState.data.data?.code) {
                        SUCCESS -> {
                            showCircularProgressBar = false
                            openDialog.value = true
                            exitOnSuccess.value = true
                            alertDialogMessage =
                                queryTransactionStateState.data.data.payments?.reason!!
                            alertDialogHeaderMessage = "Success"
                            queryData = it.data
                            return@let
                        }
                        PENDING_CODE -> {
                            transactionViewModel.queryTransaction(
                                merchantDetailsData.payload?.paymentReference ?: ""
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

                }

                initiateBankAccountPayment.data?.let {
                    transactionViewModel.setRetry(true)
                    showCircularProgressBar = true
                    if (initiateBankAccountPayment.data.data?.code == PENDING_CODE) {
                        val paymentReferenceAfterInitiate =
                            it.data?.payments?.paymentReference ?: ""
                        redirectUrl = it.data?.payments?.redirectUrl ?: ""
                        redirectUrl(redirectUrl = redirectUrl)
                        transactionViewModel.setRetry(true)

                        if (query.value) {
                            transactionViewModel.queryTransaction(
                                paymentReferenceAfterInitiate
                            )
                            query.value = false
                        }

                    } else {
                        openDialog.value = true
                        showCircularProgressBar = false
                        alertDialogMessage =
                            initiateBankAccountPayment.data.data?.message.toString()
                        alertDialogHeaderMessage = "Failed"
                        goHome.value = true
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

                Spacer(modifier = Modifier.height(100.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(
                        onClick = { navController.navigateSingleTopNoSavedState(Debit_CreditCard.route) },
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
                Spacer(modifier = Modifier.height(100.dp))
                BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }

}

