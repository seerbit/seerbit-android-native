package com.example.seerbitsdk.ussd

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.Route
import com.example.seerbitsdk.component.SeerbitPaymentDetailHeader
import com.example.seerbitsdk.component.formatAmount
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.generateSourceIp
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.ussd.UssdBankData
import com.example.seerbitsdk.models.ussd.UssdDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.TransactionViewModel

@Composable
fun USSDSelectBanksScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState?,
    transactionViewModel: TransactionViewModel
) {
    var bankCode by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    var paymentRef by remember { mutableStateOf("") }
    var ussdCode by remember { mutableStateOf("") }
    var defaultCurrency: String = ""
    val openDialog = rememberSaveable { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    val exitOnSuccess = remember { mutableStateOf(false) }

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
                var paymentReference = merchantDetailsData.payload?.paymentReference ?: ""
                defaultCurrency = merchantDetailsData.payload?.defaultCurrency ?: ""

                var amount= merchantDetailsData.payload?.amount
                val fee =   calculateTransactionFee(merchantDetailsData, TransactionType.USSD.type, amount = amount?.toDouble()?: 0.0)
                var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }

                if(isMerchantFeeBearer(merchantDetailsData)){
                    totalAmount =amount?.toDouble()
                }


                SeerbitPaymentDetailHeader(
                    charges = fee?.toDouble() ?: 0.00,
                    amount = amount?:"",
                    currencyText = defaultCurrency,
                    "Choose your bank to start this payment",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )
                Spacer(modifier = Modifier.height(41.dp))


                    ModalDialog(
                        showDialog = openDialog,
                        alertDialogHeaderMessage = "Failed",
                        alertDialogMessage = alertDialogMessage,
                        exitOnSuccess = false,
                        onSuccess = {}
                    ) {
                        openDialog.value = false
                    }


                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }

                UssdSelectBankButton {
                    bankCode = it
                }


                val ussdDTO = UssdDTO(
                    country = merchantDetailsData.payload?.country?.nameCode ?: "",
                    bankCode = bankCode,
                    amount = totalAmount.toString(),
                    redirectUrl = "http://localhost:3002/#/",
                    productId = merchantDetailsData.payload?.productId,
                    mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                    paymentReference = paymentReference,
                    fee = merchantDetailsData.payload?.vatFee,
                    fullName = merchantDetailsData.payload?.userFullName,
                    channelType = "ussd",
                    publicKey = merchantDetailsData.payload?.publicKey,
                    source = "",
                    paymentType = "USSD",
                    sourceIP = generateSourceIp(true),
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    productDescription = merchantDetailsData.payload?.productDescription,
                    email = merchantDetailsData.payload?.emailAddress,
                    retry = transactionViewModel.retry.value,
                    ddeviceType = "Android",
                    pocketReference =merchantDetailsData.payload?.pocketReference,
                    vendorId = merchantDetailsData.payload?.vendorId
                )

                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateUssdPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState2.value
                //enter payment states


                if (initiateUssdPayment.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage  = initiateUssdPayment.errorMessage?: ""
                    transactionViewModel.resetTransactionState()
                }

                if (initiateUssdPayment.isLoading) {
                    showCircularProgressBar = true
                }

                initiateUssdPayment.data?.let {
                    transactionViewModel.setRetry(true)
                    paymentRef = it.data?.payments?.paymentReference ?: ""
                    ussdCode = it.data?.payments?.ussdDailCode ?: "no ussd code"
                    Log.d("ussdCodesss", ussdCode)
                    showCircularProgressBar = false
                    navController.navigateSingleTopNoSavedState(
                        "${Route.USSD_HOME_SCREEN}/$paymentRef/${ussdCode}"
                    )

                }




                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Pay $defaultCurrency ${formatAmount(totalAmount)}",
                    onClick = {
                        if (bankCode.isNotEmpty()) {
                            transactionViewModel.initiateUssdTransaction(ussdDTO)
                        } else {
                            openDialog.value = true
                            alertDialogMessage  = "Kindly select a bank"
                        }
                    }, !showCircularProgressBar
                )

                Spacer(modifier = Modifier.height(100.dp))


                OtherPaymentButtonComponent(
                    onOtherPaymentButtonClicked = { navController.clearBackStack(UssdSelectBank.route)
                        navController.navigatePopUpToOtherPaymentScreen("${Route.OTHER_PAYMENT_SCREEN}/${TransactionType.TRANSFER.type}") },
                    onCancelButtonClicked = {navController.navigateSingleTopNoSavedState(
                        Debit_CreditCard.route)},
                    enable = !showCircularProgressBar
                )


            }
        }
    }
}


@Composable
fun UssdSelectBankButton(modifier: Modifier = Modifier, onBankCodeSelected: (String) -> Unit) {

    var selectedText by remember { mutableStateOf("") }

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var expanded by remember { mutableStateOf(false) }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column {
        Card(
            modifier = modifier.clickable { expanded = !expanded }, elevation = 1.dp,
            border = BorderStroke(0.5.dp, Color.LightGray)
        ) {

            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = "Select Banks",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        textFieldSize = layoutCoordinates.size.toSize()
                    }
                    .fillMaxHeight().clickable { expanded = !expanded  },
                trailingIcon = {
                    Icon(imageVector = icon, contentDescription = null,
                        Modifier.clickable { expanded = !expanded })
                }

            )
        }

        Spacer(Modifier.height(10.dp))
        DropdownMenu(

            expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.width(
                with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            UssdBankData.ussdBank.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label.bankName
                    onBankCodeSelected(label.bankCode)
                    expanded = false
                }) {
                    Text(text = label.bankName)
                }
            }

        }



    }


}

fun setAmountWithCurrency() {

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDSelectBankButtonPreview() {
    SeerBitTheme {
        UssdSelectBankButton {}
    }
}

