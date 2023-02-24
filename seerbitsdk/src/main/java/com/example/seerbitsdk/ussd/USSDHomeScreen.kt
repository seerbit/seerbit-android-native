package com.example.seerbitsdk.ussd

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
    navigateToLoadingScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    paymentReference: String = "",
    onConfirmPaymentClicked: () -> Unit,
    onOtherPaymentButtonClicked: () -> Unit,
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
    var alertDialogMessage  by remember { mutableStateOf("") }
    var alertDialogHeaderMessage  by remember { mutableStateOf("") }



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
        ) {


            Column(
                modifier = modifier
                    .padding(
                        start = 21.dp,
                        end = 21.dp
                    )
                    .fillMaxWidth()

            ) {
                Spacer(modifier = Modifier.height(21.dp))
                SeerbitPaymentDetailScreen(

                    charges = merchantDetailsData.payload?.cardFee?.visa!!.toDouble(),
                    amount = "60,000.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )

                val ussdDTO = UssdDTO(
                    country = merchantDetailsData.payload.address?.country!!,
                    bankCode = bankCode,
                    amount = "60000",
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = "404",
                    paymentReference = "SBT-T54367073117",
                    fee = merchantDetailsData.payload.cardFee.mc,
                    fullName = "Amos Oruaroghene",
                    channelType = "ussd",
                    publicKey = "SBTESTPUBK_t4G16GCA1O51AV0Va3PPretaisXubSw1",
                    source = "",
                    paymentType = "USSD",
                    sourceIP = "102.88.63.64",
                    currency = merchantDetailsData.payload.defaultCurrency,
                    productDescription = "",
                    email = "inspiron.amos@gmail.com",
                    retry = true,
                    ddeviceType = "Desktop"
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
                    ErrorDialog(
                        message = initiateUssdPayment.errorMessage
                            ?: "Something went wrong"
                    )
                    retryCount++
                }
                showCircularProgressBar = initiateUssdPayment.isLoading

                initiateUssdPayment.data?.let {
                    val paymentReference2 = it.data?.payments?.paymentReference
                    if (!isSuccesfulResponse) {
                        ussdCode = it.data?.payments?.ussdDailCode.toString()
                    }
                    isSuccesfulResponse = true


                }

                //querying transaction happens after otp has been inputted
                if (queryTransactionStateState.hasError) {
                    showLoadingScreen= false
                    ErrorDialog(
                        message = queryTransactionStateState.errorMessage ?: "Something went wrong"
                    )
                }
                if (queryTransactionStateState.isLoading) {
                }

                if(queryTransactionStateState.data?.data!= null){

                    if (queryTransactionStateState.data.data.code == PENDING_CODE) {
                        transactionViewModel.queryTransaction(ussdDTO.paymentReference!!)
                    }
                    if(queryTransactionStateState.data.data.code == SUCCESS){
                        showLoadingScreen = false
                        alertDialogMessage = queryTransactionStateState.data.data.message!!
                        alertDialogHeaderMessage = "Success"
                    }
                    if(queryTransactionStateState.data.data.code == "SM_X23" || queryTransactionStateState.data.data.code == "S12"){
                        showLoadingScreen = false
                        alertDialogMessage = queryTransactionStateState.data.data.message!!
                        alertDialogHeaderMessage = "Failed"

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
                                transactionViewModel.queryTransaction(ussdDTO.paymentReference!!)

                            }

                        },
                        !showCircularProgressBar
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    showLoaderLayout()
                }

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
            navigateToLoadingScreen = { /*TODO*/ },
            currentDestination = null,
            navController = rememberNavController(),
            onConfirmPaymentClicked = {
            },
            onOtherPaymentButtonClicked = { },
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