package com.example.seerbitsdk.bank

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.*
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.google.gson.Gson

@Composable
fun BankAccountDOBScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankAccountNumber: String,
    bvn: String,
    bankCode: String,
    requiredFields: RequiredFields?,
    bankName: String?,
    actionListener: ActionListener?,

    ) {
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var json by remember { mutableStateOf(Uri.encode(Gson().toJson(requiredFields))) }
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

                var amount = merchantDetailsData.payload?.amount
                val currency = merchantDetailsData.payload?.defaultCurrency?:""
                val fee =  calculateTransactionFee(merchantDetailsData, TransactionType.ACCOUNT.type, amount = amount?.toDouble()?:0.0)
                var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }

                if(isMerchantFeeBearer(merchantDetailsData)){
                    totalAmount =amount?.toDouble()
                }

                SeerbitPaymentDetailHeader(
                    charges = fee?.toDouble() ?: 0.0,
                    amount = amount?:"",
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    "Please Enter your birthday",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )

                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""
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
                    channelType = "$bankName",
                    dateOfBirth = dob,
                    publicKey = merchantDetailsData.payload?.publicKey,
                    source = "",
                    accountName = merchantDetailsData.payload?.userFullName,
                    paymentType = "ACCOUNT",
                    sourceIP = generateSourceIp(true),
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    bvn = bvn,
                    email = merchantDetailsData.payload?.emailAddress,
                    productDescription = merchantDetailsData.payload?.productDescription,
                    scheduleId = "",
                    accountNumber = bankAccountNumber,
                    retry = transactionViewModel.retry.value,
                    pocketReference =merchantDetailsData.payload?.pocketReference,
                    vendorId = merchantDetailsData.payload?.vendorId
                )

                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = false
                ) {
                    openDialog.value = false
                }

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


                initiateBankAccountPayment.data?.let {
                    showCircularProgressBar = true
                    transactionViewModel.setRetry(true)
                    if (initiateBankAccountPayment.data.data?.code == SUCCESS) {
                        val linkingReference = it.data?.payments?.linkingReference
                        navController.navigateSingleTopNoSavedState(
                            "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$bankAccountNumber/$bvn/$dob/$linkingReference"
                        )

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



                Spacer(modifier = modifier.height(10.dp))
                //Show Birthday
                BirthDayInputField(Modifier, "DD / MM / YYYY ") {
                    dob = it
                }

                Spacer(modifier = modifier.height(30.dp))

                AuthorizeButton(
                    buttonText = "Pay $currency${formatInputDouble(totalAmount.toString())}",
                    onClick = {

                        if (dob.isNotEmpty()) {
                            transactionViewModel.initiateTransaction(bankAccountDTO)
                        }
                        else {
                            openDialog.value = true
                            showCircularProgressBar = false
                            alertDialogMessage = "Invalid Date of Birth"
                            alertDialogHeaderMessage = "Failed"
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
fun BankAccountDOBScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

    }
}


@Composable
fun BirthDayInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBirthday: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 1.dp,
            border = BorderStroke(0.5.dp, Color.LightGray)
        ) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.length <= 8)
                        value = newText
                    onEnterBirthday(newText)
                },
                visualTransformation = { birthdayInputFormat(it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Send
                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}


fun birthdayInputFormat(text: AnnotatedString): TransformedText {

    // change the length
    val annotatedString = AnnotatedString.Builder().run {
        for (i in text.indices) {
            append(text[i])
            if (i == 1 || i == 3) {
                append("/")
            }
        }
        toAnnotatedString()
    }


    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset + 1
            if (offset <= 7) return offset + 2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset - 1
            if (offset <= 7) return offset - 2
            return 8
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


