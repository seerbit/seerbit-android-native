package com.example.seerbitsdk.ussd

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.query.QueryData
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.*
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun USSDHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState?,
    transactionViewModel: TransactionViewModel,
    paymentReference: String?,
    ussdCode: String?,
    onCancelButtonClicked: () -> Unit,
    onOtherPaymentButtonClicked: () -> Unit,
    actionListener: ActionListener?
) {
    var showLoadingScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = rememberSaveable { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    var queryData : QueryData? = null





    Column(modifier = modifier) {
        // if there is an error loading the report
        if (merchantDetailsState?.hasError!!) {
            ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
        }
        if (merchantDetailsState.isLoading) {
            showCircularProgress(showProgress = true)
        }

        if (merchantDetailsState.data != null) {
            val merchantDetailsData = merchantDetailsState.data
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()

            ) {

                var amount = merchantDetailsData.payload?.amount
                val fee = calculateTransactionFee(
                    merchantDetailsData,
                    TransactionType.USSD.type,
                    amount = amount?.toDouble() ?: 0.0
                )
                val totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
                var onSuccess: () -> Unit

                Spacer(modifier = Modifier.height(21.dp))
                SeerbitPaymentDetailHeader(

                    charges = fee?.toDouble() ?: 0.0,
                    amount = amount ?: "",
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    "",
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
                }

                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value


                //querying transaction happens after otp has been inputted
                if (queryTransactionStateState.hasError) {
                    showLoadingScreen = false
                    openDialog.value = true
                    alertDialogMessage = "This action could not be completed"
                    alertDialogHeaderMessage = "Failed"

                }
                if (queryTransactionStateState.isLoading) {
                }

                queryTransactionStateState.data?.data?.let {

                    when (queryTransactionStateState.data.data.code) {
                        PENDING_CODE -> {
                            transactionViewModel.queryTransaction(paymentReference ?: "")
                        }
                        SUCCESS -> {
                            openDialog.value = true
                            alertDialogMessage =  ""//queryTransactionStateState.data.data.message ?:
                            alertDialogHeaderMessage = "Transaction Successful!"
                            alertDialogMessage = queryTransactionStateState.data.data.message ?: ""
                            alertDialogHeaderMessage = "Success!"
                            queryData = it
                            showLoadingScreen = false
                            exitOnSuccess.value = true
                            return@let

                        }
                        else -> {
                            openDialog.value = true
                            alertDialogMessage = queryTransactionStateState.data.data.message ?: ""
                            alertDialogHeaderMessage = "Failed"
                            showLoadingScreen = false
                            transactionViewModel.resetTransactionState()
                            return@let
                        }
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
                    USSDCodeSurfaceView(LocalContext.current, ussdCodeText = ussdCode ?: "")
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

                                copyToClipboard(context, ussdCode ?: "")
                                Toast
                                    .makeText(context, "Ussd code copied!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    AuthorizeButton(
                        buttonText = "Confirm Payment",
                        onClick = {
                            showLoadingScreen = true
                            transactionViewModel.queryTransaction(paymentReference ?: "")


                        },
                        !showCircularProgressBar
                    )

                    Spacer(modifier = Modifier.height(100.dp))

                    OtherPaymentButtonComponent(
                        onOtherPaymentButtonClicked = { navController.navigatePopUpToOtherPaymentScreen("${Route.OTHER_PAYMENT_SCREEN}/${TransactionType.USSD.type}") },
                        onCancelButtonClicked = onCancelButtonClicked,
                        enable = !showCircularProgressBar
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                    BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

                    Spacer(modifier = Modifier.height(16.dp))


                } else {
                    showLoaderLayout()
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
            navController = rememberNavController(),
            merchantDetailsState = MerchantDetailsState(),
            transactionViewModel = viewModel,
            paymentReference = "",
            ussdCode = "",
            onCancelButtonClicked = {},
            onOtherPaymentButtonClicked = {},
            actionListener = null
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


@Composable
fun ModalDialog(
    context: Context = LocalContext.current,
    showDialog: MutableState<Boolean>,
    alertDialogHeaderMessage: String,
    alertDialogMessage: String, exitOnSuccess: Boolean,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    //The alert dialog occurs here
    val activity = context as? Activity

    if (showDialog.value) {
        AlertDialog(
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = alertDialogHeaderMessage, style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),

                    )
                }

            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        alertDialogMessage,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        softWrap = true,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }

            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (alertDialogHeaderMessage == "Failed" || alertDialogHeaderMessage == "Error" || alertDialogHeaderMessage == "Error Occurred")
                        Image(
                            painter = painterResource(id = R.drawable.failed),
                            contentDescription = "", modifier = Modifier.size(60.dp)
                        )
                    else if (alertDialogHeaderMessage.contains("Success", ignoreCase = true)) {
                        Image(
                            painter = painterResource(id = R.drawable.success),
                            contentDescription = "", modifier = Modifier.size(120.dp)
                        )
                    } else Image(
                        painter = painterResource(id = R.drawable.failed),
                        contentDescription = "", modifier = Modifier.size(60.dp)
                    )

                }
                Button(
                    onClick = {
                        onDismiss()
                        if (exitOnSuccess) {
                            activity?.finish()
                            onSuccess()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black
                    )
                ) {
                    Text(text = "Close", style = TextStyle(color = Color.White))

                }


            },
        )
    }
}


@Composable
fun ErrorDialogWithRetry(
    context: Context = LocalContext.current, showDialog: MutableState<Boolean>,
    alertDialogHeaderMessage: String,
    alertDialogMessage: String, exitOnSuccess: Boolean,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit?,
    onRetry: () -> Unit,
) {
    //The alert dialog occurs here
    val activity = context as? Activity

    if (showDialog.value) {
        AlertDialog(
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = alertDialogHeaderMessage, style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        alertDialogMessage, style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (alertDialogHeaderMessage == "Failed" || alertDialogHeaderMessage == "Error" || alertDialogHeaderMessage == "Error Occurred")
                        Image(
                            painter = painterResource(id = R.drawable.failed),
                            contentDescription = "", modifier = Modifier.size(60.dp)
                        )
                    else if (alertDialogHeaderMessage.contains("Success", ignoreCase = true)) {
                        Image(
                            painter = painterResource(id = R.drawable.success),
                            contentDescription = "", modifier = Modifier.size(120.dp)
                        )
                    } else Image(
                        painter = painterResource(id = R.drawable.failed),
                        contentDescription = "", modifier = Modifier.size(60.dp)
                    )

                }

                Button(
                    onClick = {
                        if (exitOnSuccess) {
                            activity?.finish()
                            onSuccess()
                        } else {
                            onRetry()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black
                    )
                ) {
                    val text: String = if (exitOnSuccess) {
                        "Close"
                    } else "Retry"
                    Text(text = text, style = TextStyle(color = Color.White))
                }

            },
        )
    }
}
