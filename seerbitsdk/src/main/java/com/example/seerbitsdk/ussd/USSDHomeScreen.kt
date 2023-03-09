package com.example.seerbitsdk.ussd

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.ussd.UssdDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun USSDHomeScreen(
    modifier: Modifier = Modifier,
    merchantDetailsState: MerchantDetailsState?,
    transactionViewModel: TransactionViewModel = viewModel(),
    bankCode: String?

) {
    var showLoadingScreen by remember { mutableStateOf(false) }
    var ussdCode by remember { mutableStateOf("") }
    var isSuccesfulResponse by remember { mutableStateOf(false) }
    var retryCount by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var showCircularProgressBar by rememberSaveable { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }

    var paymentRef by remember { mutableStateOf("") }


    Column(modifier = modifier) {
        // if there is an error loading the report
        if (merchantDetailsState?.hasError!!) {
            ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
        }
        if (merchantDetailsState.isLoading) {
            showCircularProgress(showProgress = true)
        }


        merchantDetailsState.data?.let { merchantDetailsData ->

            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 21.dp,
                        end = 21.dp
                    )
                    .fillMaxWidth()

            ) {



                Spacer(modifier = Modifier.height(21.dp))
                SeerbitPaymentDetailHeader(

                    charges = merchantDetailsData.payload?.vatFee?.toDouble()!!,
                    amount = "20.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )

                val ussdDTO = UssdDTO(
                    country = merchantDetailsData.payload.country?.nameCode?:"",
                    bankCode = bankCode,
                    amount = "20",
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload.number,
                    paymentReference  = transactionViewModel.generateRandomReference(),
                    fee = merchantDetailsData.payload.vatFee,
                    fullName = merchantDetailsData.payload.businessName,
                    channelType = "ussd",
                    publicKey = "SBPUBK_TCDUH6MNIDLHMJXJEJLBO6ZU2RNUUPHI",
                    source = "",
                    paymentType = "USSD",
                    sourceIP = "102.88.63.64",
                    currency = merchantDetailsData.payload.defaultCurrency,
                    productDescription = "",
                    email ="sdk@gmail.com",
                    retry = false,
                    ddeviceType = "Android"
                )

                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value

                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateUssdPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState.value
                //enter payment states

                if (initiateUssdPayment.data == null && !isSuccesfulResponse) {
                    transactionViewModel.initiateTransaction(ussdDTO)
                }

                if (initiateUssdPayment.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage = queryTransactionStateState.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Failed"
                    transactionViewModel.resetTransactionState()
                    isSuccesfulResponse = true
                }

                if(initiateUssdPayment.isLoading) {
                    showCircularProgressBar = true
                }

                initiateUssdPayment.data?.let {
                    paymentRef = it.data?.payments?.paymentReference!!
                    if (!isSuccesfulResponse) {
                        ussdCode = it.data?.payments?.ussdDailCode.toString()
                    }
                    showCircularProgressBar = false
                    isSuccesfulResponse = true

                }

                //querying transaction happens after otp has been inputted
                if (queryTransactionStateState.hasError) {
                    showLoadingScreen = false
                    openDialog.value = true
                    alertDialogMessage =
                        queryTransactionStateState.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Failed"
                }
                if (queryTransactionStateState.isLoading) {
                }

                queryTransactionStateState.data?.data?.let {

                    if (queryTransactionStateState.data.data.code == PENDING_CODE) {
                        transactionViewModel.queryTransaction(paymentRef)
                    }
                    if (queryTransactionStateState.data.data.code == SUCCESS) {

                        openDialog.value = true
                        alertDialogMessage = queryTransactionStateState.data.data.message!!
                        alertDialogHeaderMessage = "Success"
                        showLoadingScreen = false
                        transactionViewModel.resetTransactionState()
                        return@let

                    }
                    if (queryTransactionStateState.data.data.code == "SM_X23" || queryTransactionStateState.data.data.code == "S12") {

                        openDialog.value = true
                        alertDialogMessage = queryTransactionStateState.data.data.message!!
                        alertDialogHeaderMessage = "Failed"
                        showLoadingScreen = false
                        transactionViewModel.resetTransactionState()
                        return@let

                    }
                }

                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }
                // if loadingScreen value is false
                if (!showLoadingScreen) {
                    Text(
                        text = "Dial the code below to complete this payment.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Faktpro,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 10.sp
                        ),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    USSDCodeSurfaceView(LocalContext.current, ussdCodeText = ussdCode)
                    Spacer(modifier = modifier.height(20.dp))

                    Text(
                        text = "Click to copy code.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Faktpro,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 10.sp
                        ),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .clickable {
                                copyToClipboard(context, ussdCode)
                                Toast
                                    .makeText(context, "Ussd code copied!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    AuthorizeButton(
                        buttonText = "Confirm Payment",
                        onClick = {
                            if (isSuccesfulResponse) {
                                showLoadingScreen = true
                                transactionViewModel.queryTransaction(paymentRef)

                            }

                        },
                        !showCircularProgressBar
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    showLoaderLayout()
                }

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
                                Text(text = alertDialogHeaderMessage)
                            }

                        },
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(alertDialogMessage)
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


@Composable
fun USSDCodeSurfaceView(context: Context?, ussdCodeText: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        color = LighterGray
    ) {
        Row(horizontalArrangement = Arrangement.Center) {

            Text(
                text = ussdCodeText, style = TextStyle(
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp
                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp)
                    .clickable {
                    }
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDCodeSurfaceViewPreview() {
    SeerBitTheme {
        USSDCodeSurfaceView(LocalContext.current, ussdCodeText = "*737*000*99099#")
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {

    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {
        USSDHomeScreen(
            merchantDetailsState = MerchantDetailsState(),
            transactionViewModel = viewModel,
            bankCode = ""
        )
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ussdCode", text)
    clipboardManager.setPrimaryClip(clip)

}

@Composable
fun showLoaderLayout() {
    Text(
        text = "Hold on tight while we confirm your payment.",
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            lineHeight = 10.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    CustomLinearProgressBar(showProgress = true)
    Spacer(modifier = Modifier.height(25.dp))


}