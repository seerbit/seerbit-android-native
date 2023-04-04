package com.example.seerbitsdk.transfer

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.Debit_CreditCard
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.navigateSingleTopNoSavedState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.*
import com.example.seerbitsdk.ussd.USSDCodeSurfaceView
import com.example.seerbitsdk.ussd.copyToClipboard
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun TransferHomeScreen(
    modifier: Modifier = Modifier,
    merchantDetailsState: MerchantDetailsState?,
    transactionViewModel: TransactionViewModel = viewModel(),
    paymentRef: String?,
    navController: NavHostController,
    walletName: String?,
    bankName: String?,
    accountNumber: String?
) {

    var transferAmount by remember { mutableStateOf("") }
    var showCircularProgressBar by rememberSaveable { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)


    // if there is an error loading the report
    if (merchantDetailsState?.hasError!!) {
        ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
    }

    if (merchantDetailsState.isLoading) {
        showCircularProgressBar = true
    }

    merchantDetailsState.data?.let { merchantDetailsData ->

        Column(
            modifier = modifier
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {


            //HANDLES initiate query response
            val queryTransactionStateState: QueryTransactionState =
                transactionViewModel.queryTransactionState.value
            //HANDLE INITIATE TRANSACTION RESPONSE

            //enter payment states

            val defaultCurrency: String = merchantDetailsData.payload?.defaultCurrency ?: ""

            var amount= merchantDetailsData.payload?.amount
            val fee =   calculateTransactionFee(merchantDetailsData, TransactionType.TRANSFER.type, amount = amount?.toDouble()?: 0.0)
            val totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }

            transferAmount = formatAmount(amount = amount?.toDouble())

            Spacer(modifier = Modifier.height(21.dp))

            SeerbitPaymentDetailHeaderTwo(
                charges = fee?.toDouble()?:0.0,
                amount = amount.toString(),
                currencyText = defaultCurrency,
                businessName = merchantDetailsData.payload?.businessName?:"",
                email = merchantDetailsData.payload?.supportEmail?:""
            )

            //querying transaction happens after otp has been inputted
            if (queryTransactionStateState.hasError) {
                showCircularProgressBar = false
                openDialog.value = true
                alertDialogMessage =
                    queryTransactionStateState.data?.data?.message
                        ?: "Could not retrieve bank details"
                alertDialogHeaderMessage = "Failed"
                transactionViewModel.resetTransactionState()

            }
            if (queryTransactionStateState.isLoading) {
                showCircularProgressBar = true

            }


            if (queryTransactionStateState.data?.data != null) {
                when (queryTransactionStateState.data.data.code) {


                    PENDING_CODE -> {
                        showCircularProgressBar = true
                        transactionViewModel.queryTransaction(paymentRef ?: "")
                    }
                    SUCCESS -> {
                        alertDialogHeaderMessage = "Successful"
                        openDialog.value = true
                        alertDialogMessage = queryTransactionStateState.data.data.payments?.reason!!
                        showCircularProgressBar = false
                        exitOnSuccess.value = true
                        transactionViewModel.resetTransactionState()
                    }

                    else -> {
                        alertDialogHeaderMessage = "Failed"
                        openDialog.value = true
                        alertDialogMessage = queryTransactionStateState.data.data.payments?.reason!!
                        showCircularProgressBar = false
                        transactionViewModel.resetTransactionState()
                    }
                }
            }


            Text(
                text = "Transfer the exact amount including decimals",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                    color = DeepRed,
                    textAlign = TextAlign.Center

                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(10.dp)
            )


            Spacer(modifier = Modifier.height(20.dp))
            USSDCodeSurfaceView(null, ussdCodeText = "$defaultCurrency${formatAmount(totalAmount)}")
            Spacer(modifier = modifier.height(20.dp))

            Text(
                text = "To",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Row(modifier.padding(12.dp)) {
                AccountDetailsSurfaceView(accountNumber ?: "", bankName ?: "", walletName ?: "")
            }


            Text(
                text = "Account number can only be used once",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                    color = DeepRed,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )


            Spacer(modifier = modifier.height(10.dp))

            if (showCircularProgressBar) {
                showCircularProgress(showProgress = true)
            }
            Spacer(modifier = Modifier.height(10.dp))


            AuthorizeButton(
                buttonText = "I have completed this bank transfer",
                onClick = {
                    transactionViewModel.queryTransaction(paymentRef ?: "")

                }, !showCircularProgressBar
            )
            Spacer(modifier = Modifier.height(50.dp))


            //put your alert dialog here
            //The alert dialog occurs here
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {

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
                                if (exitOnSuccess.value) {
                                    activity?.finish()
                                }

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


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun TransferHomeScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

        TransferHomeScreen(
            merchantDetailsState = MerchantDetailsState(),
            transactionViewModel = viewModel,
            paymentRef = "",
            navController = rememberNavController(),
            walletName = "walletName",
            bankName = "bankName",
            accountNumber = "accountNumber"
        )
    }
}

@Composable
fun AccountDetailsSurfaceView(accountNumber: String, bankName: String, walletName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                top = 16.dp,
                end = 0.dp,
                start = 0.dp,
                bottom = 16.dp
            ),
        shape = RoundedCornerShape(8.dp),
        color = LighterGray
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            CustomAccountDetailsRow(
                leftHandText = "Account Number",
                rightHandText = accountNumber,
                icon = R.drawable.ic_copy
            )
            CustomAccountDetailsRow(
                LocalContext.current,
                leftHandText = "Bank",
                rightHandText = bankName,
                icon = null
            )
            CustomAccountDetailsRow(
                leftHandText = "Beneficiary Name",
                rightHandText = walletName,
                icon = null
            )
            CustomAccountDetailsRow(
                leftHandText = "Validity",
                rightHandText = "30 Minutes",
                icon = null
            )

        }
    }
}

@Composable
fun CustomAccountDetailsRow(
    context: Context = LocalContext.current,
    leftHandText: String,
    rightHandText: String,
    icon: Int?
) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftHandText,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp,
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = rightHandText,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                ),
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            copyToClipboard(context, rightHandText)
                            Toast
                                .makeText(context, "Account Number copied", Toast.LENGTH_SHORT)
                                .show()

                        }
                )
            }
        }
    }

}
