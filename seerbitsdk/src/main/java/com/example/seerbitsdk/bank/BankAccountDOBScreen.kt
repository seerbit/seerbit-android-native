package com.example.seerbitsdk.bank

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.navigateSingleTopNoSavedState
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ui.theme.SignalRed
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
    bankName : String?,

) {
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var paymentRef = transactionViewModel.generateRandomReference()
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
                        start = 21.dp,
                        end = 21.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                var amount: String = merchantDetailsData.payload?.amount ?: ""
                SeerbitPaymentDetailHeader(
                    charges =  merchantDetailsData.payload?.vatFee?.toDouble()?:0.0,
                    amount = amount,
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    "Please Enter your birthday",
                    merchantDetailsData.payload?.businessName?:"",
                    merchantDetailsData.payload?.supportEmail?:""
                )

                val bankAccountDTO = BankAccountDTO(
                    deviceType = "Android",
                    country = merchantDetailsData.payload?.country?.countryCode?: "",
                    bankCode = bankCode,
                    amount = amount,
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                    paymentReference = paymentRef,
                    fee = merchantDetailsData.payload?.vatFee,
                    fullName =  merchantDetailsData.payload?.userFullName,
                    channelType = "$bankName",
                    dateOfBirth = dob,
                    publicKey = merchantDetailsData.payload?.publicKey,
                    source = "",
                    accountName = merchantDetailsData.payload?.userFullName,
                    paymentType = "ACCOUNT",
                    sourceIP = "128.0.0.1",
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    bvn = bvn,
                    email = merchantDetailsData.payload?.emailAddress,
                    productDescription = "",
                    scheduleId = "",
                    accountNumber = bankAccountNumber,
                    retry = false
                )

                if(showCircularProgressBar){
                    showCircularProgress(showProgress = true)
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
                    if (initiateBankAccountPayment.data.data?.code== SUCCESS) {
                        val linkingReference = it.data?.payments?.linkingReference

                        navController.navigateSingleTopNoSavedState(
                            "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$bankAccountNumber/$bvn/$dob/$linkingReference"
                        )

                    } else  {
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
                    buttonText = "Pay NGN$amount",
                    onClick = {

                        if (dob.isNotEmpty()) {
                            transactionViewModel.initiateTransaction(bankAccountDTO)
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


        Card(modifier = modifier, elevation = 1.dp) {
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
            if (offset <= 7) return offset -2
            return 8
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


