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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.formatInputDouble
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.google.gson.Gson

@Composable
fun BankAccountBVNScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankAccountNumber: String,
    bankCode: String?,
    requiredFields : RequiredFields?,
    bankName: String?
    ) {

    var bvn by remember { mutableStateOf("") }
    var json by remember { mutableStateOf(Uri.encode(Gson().toJson(requiredFields))) }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }

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

                    charges =  fee?.toDouble()?:0.0,
                    amount = amount?:"",
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    "Kindly Enter your BVN",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = false
                ) {

                }


                Spacer(modifier = modifier.height(10.dp))

                BVNInputField(Modifier, "Enter your BVN Number") {
                    bvn = it
                }

                Spacer(modifier = modifier.height(20.dp))

                Spacer(modifier = modifier.height(10.dp))
                AuthorizeButton(
                    buttonText = "Pay $currency${formatInputDouble(totalAmount.toString()) }",
                    onClick = {
                        if (bvn.isNotEmpty()) {

                            requiredFields?.let {

                                if (it.dateOfBirth == YES){
                                    navController.navigateSingleTopNoSavedState(
                                        "${Route.BANK_ACCOUNT_DOB_SCREEN}/$bankName/$json/$bankCode/$bankAccountNumber/$bvn"
                                    )
                                }
                                else{
                                    navController.navigateSingleTopNoSavedState(
                                        "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$bankAccountNumber/$bvn/$Dummy/$Dummy"
                                    )
                                }
                            }
                        }else {
                            openDialog.value = true
                            showCircularProgressBar = false
                            alertDialogMessage = "Invalid BVN"
                            alertDialogHeaderMessage = "Failed"
                        }
                    }, true
                )
                Spacer(modifier = Modifier.height(100.dp))

                OtherPaymentButtonComponent(
                    onOtherPaymentButtonClicked = { navController.navigatePopUpToOtherPaymentScreen("${Route.OTHER_PAYMENT_SCREEN}/dummy") },
                    onCancelButtonClicked = {navController.navigateSingleTopTo(Debit_CreditCard.route)},
                    enable = true
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
fun BankAccountBVNScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

    }
}


@Composable
fun BVNInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBVN: (String) -> Unit
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
                    if (newText.length <= 11)
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



