package com.example.seerbitsdk.bank

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.google.gson.Gson


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BankAccountNumberScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankCode: String?,
    requiredFields: RequiredFields?,
    bankName : String?
) {


    var accountNumber by remember {
        mutableStateOf("")
    }

    var json by remember { mutableStateOf(Uri.encode(Gson().toJson(requiredFields))) }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    var paymentRef = transactionViewModel.generateRandomReference()
    val keyboardController = LocalSoftwareKeyboardController.current

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

                var amount : String = merchantDetailsData.payload?.amount?:""
                SeerbitPaymentDetailHeader(

                    charges =  merchantDetailsData.payload?.vatFee?.toDouble()?:0.0,
                    amount = amount,
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    "Please Enter your Account Number",
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
                    fullName = merchantDetailsData.payload?.userFullName,
                    channelType = "$bankName",
                    dateOfBirth = "",
                    publicKey = merchantDetailsData.payload?.publicKey,
                    source = "",
                    accountName = merchantDetailsData.payload?.userFullName,
                    paymentType = "ACCOUNT",
                    sourceIP = "128.0.0.1",
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    bvn = "",
                    email = merchantDetailsData.payload?.emailAddress,
                    productDescription = "",
                    scheduleId = "",
                    accountNumber = accountNumber,
                    retry = false
                )


                val initiateBankAccountPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState.value
                //enter payment states

                if(initiateBankAccountPayment.isLoading){
                    showCircularProgressBar = true
                }


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

                    if (initiateBankAccountPayment.data.data?.code== PENDING_CODE) {
                        val linkingReference = it.data?.payments?.linkingReference
                        showCircularProgressBar = false
                        navController.navigateSingleTopNoSavedState(
                            "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$accountNumber/$Dummy/$Dummy/$linkingReference"
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

                if(showCircularProgressBar){
                    showCircularProgress(showProgress = true)
                }

                Spacer(modifier = Modifier.height(21.dp))
                BankAccountNumberField(Modifier, "10 Digit Bank Account Number") {
                    accountNumber = it
                }

                Spacer(modifier = modifier.height(20.dp))

                Spacer(modifier = modifier.height(10.dp))
                AuthorizeButton(
                    buttonText = "Verify Account",
                    onClick = {

                        keyboardController?.hide()
                        if (accountNumber.isNotEmpty() && accountNumber.length == 10) {

                            requiredFields?.let {

                                if(it.bvn == NO&&it.dateOfBirth == NO&&it.bvn == NO){
                                    transactionViewModel.initiateTransaction(bankAccountDTO)
                                    return@let
                                }
                                if (it.bvn == YES) {
                                    navController.navigateSingleTopNoSavedState(
                                        "${Route.BANK_ACCOUNT_BVN_SCREEN}/$bankName/$json/$bankCode/$accountNumber"
                                    )
                                }
                                else if (it.dateOfBirth == YES){
                                    navController.navigateSingleTopNoSavedState(
                                        "${Route.BANK_ACCOUNT_DOB_SCREEN}/$bankName/$json/$bankCode/$accountNumber/$Dummy"
                                    )
                                }
                                else{
                                    navController.navigateSingleTopNoSavedState(
                                        "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$accountNumber/$Dummy/$Dummy"
                                    )
                                }

                            }

                        }


                    }, !showCircularProgressBar
                )

            }


        }
    }

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun BankAccountNumberScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

    }
}


@Composable
fun BankAccountNumberField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBVN: (String) -> Unit
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
                    if (newText.length <= 10)
                        value = newText
                    onEnterBVN(newText)
                },

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



