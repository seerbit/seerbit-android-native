package com.example.seerbitsdk.transfer

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.PENDING_CODE
import com.example.seerbitsdk.component.SUCCESS
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.USSDCodeSurfaceView
import com.example.seerbitsdk.ussd.copyToClipboard
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun TransferHomeScreen(
    modifier: Modifier = Modifier,
    navigateToLoadingScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    paymentReference: String = "",
    onCancelPaymentButtonClicked: () -> Unit,
    onOtherPaymentButtonClicked: () -> Unit,
    merchantDetailsState: MerchantDetailsState?,
    transactionViewModel: TransactionViewModel = viewModel()

) {

    var showLoadingScreen by remember { mutableStateOf(false) }
    var transferAmount by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("") }
    var wallet by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var isSuccesfulResponse by remember { mutableStateOf(false) }
    var retryCount by remember { mutableStateOf(0) }
    var showCircularProgressBar by rememberSaveable { mutableStateOf(false) }


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
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(21.dp))

            SeerbitPaymentDetailScreen(
                charges = merchantDetailsData.payload?.cardFee?.visa!!.toDouble(),
                amount = "60000",
                currencyText = merchantDetailsData.payload.defaultCurrency!!,
                "",
                merchantDetailsData.payload.businessName!!,
                merchantDetailsData.payload.supportEmail!!
            )

            val transferDTO = TransferDTO(
                country = merchantDetailsData.payload.address?.country!!,
                bankCode = "044",
                amount = "60000",
                productId = "",
                mobileNumber = "404",
                paymentReference = "SBT-T54367073117",
                fee = merchantDetailsData.payload.cardFee.mc,
                fullName = "Amos Oruaroghene",
                channelType = "Transfer",
                publicKey = merchantDetailsData.payload.testPublicKey,
                source = "",
                paymentType = "TRANSFER",
                sourceIP = "102.88.63.64",
                currency = merchantDetailsData.payload.defaultCurrency,
                productDescription = "",
                email = "inspiron.amos@gmail.com",
                retry = true,
                deviceType = "Desktop",
                amountControl = "FIXEDAMOUNT",
                walletDaysActive = "1"
            )
            transferAmount = transferDTO.amount.toString()

            //HANDLES initiate query response
            val queryTransactionStateState: QueryTransactionState =
                transactionViewModel.queryTransactionState.value
            //HANDLE INITIATE TRANSACTION RESPONSE
            val initiateTransferPayment: InitiateTransactionState =
                transactionViewModel.initiateTransactionState.value
            //enter payment states

            if (initiateTransferPayment.data == null && !isSuccesfulResponse) {
                transactionViewModel.initiateTransaction(transferDTO)
            }

            if (initiateTransferPayment.hasError) {
                ErrorDialog(
                    message = initiateTransferPayment.errorMessage
                        ?: "Something went wrong"
                )
            }
            if (initiateTransferPayment.isLoading) {
                showCircularProgress(showProgress = true)
            }
            initiateTransferPayment.data?.let {
                if (!isSuccesfulResponse) {
                    wallet = it.data?.payments?.wallet!!
                    walletName = it.data.payments.walletName!!
                    bankName = it.data.payments.bankName!!
                    accountNumber = it.data.payments.accountNumber!!
                }
                isSuccesfulResponse = true
            }

            //querying transaction happens after otp has been inputted
            if (queryTransactionStateState.hasError) {
                ErrorDialog(
                    message = queryTransactionStateState.errorMessage ?: "Something went wrong"
                )
            }
            if (queryTransactionStateState.isLoading) {
                showCircularProgressBar = true
            }


            if(queryTransactionStateState.data?.data!= null){

                if (queryTransactionStateState.data.data.code == PENDING_CODE) {
                    transactionViewModel.queryTransaction(transferDTO.paymentReference!!)
                }
                if(queryTransactionStateState.data.data.code == SUCCESS){
                    ErrorDialog(message = queryTransactionStateState.data.data.payments?.reason!!)
                    showCircularProgressBar = false
                    showLoadingScreen = false
                    //alertDialogMessage = queryTransactionStateState.data.data.payments.reason!!
                    //alertDialogHeaderMessage = "Success"
                }
                if(queryTransactionStateState.data.data.code == "SM_X23" || queryTransactionStateState.data.data.code == "S12"){
                    ErrorDialog(message = queryTransactionStateState.data.data.payments?.reason!!)
                    showCircularProgressBar = false
                    //alertDialogMessage = queryTransactionStateState.data.data.payments?.reason!!
                    //alertDialogHeaderMessage = "Failed"

                }
            }


            Text(
                text = "Transfer the exact amount including decimals",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp,
                    color = DeepRed,
                    textAlign = TextAlign.Center

                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(10.dp)
            )


            Spacer(modifier = Modifier.height(20.dp))
            USSDCodeSurfaceView(null, ussdCodeText = transferAmount)
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

            if (isSuccesfulResponse) {
                Row(modifier.padding(12.dp)) {
                    AccountDetailsSurfaceView(accountNumber, bankName, walletName)
                }
            }

            Text(
                text = "Account number can only be used once",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp,
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
                    if (isSuccesfulResponse) {
                        transactionViewModel.queryTransaction(transferDTO.paymentReference!!)
                    }
                }, !showCircularProgressBar
            )
            Spacer(modifier = Modifier.height(50.dp))

        }


    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun TransferHomeScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

        TransferHomeScreen(
            navigateToLoadingScreen = { /*TODO*/ },
            currentDestination = null,
            navController = rememberNavController(),
            onCancelPaymentButtonClicked = { /*TODO*/ },
            onOtherPaymentButtonClicked = { /*TODO*/ },
            merchantDetailsState = MerchantDetailsState(),
            transactionViewModel = viewModel
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
                    lineHeight = 10.sp,
                )
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
