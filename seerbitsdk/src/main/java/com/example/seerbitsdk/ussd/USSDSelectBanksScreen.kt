package com.example.seerbitsdk.ussd

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
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
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.Route
import com.example.seerbitsdk.component.SeerbitPaymentDetailHeader
import com.example.seerbitsdk.models.ussd.UssdBankData
import com.example.seerbitsdk.models.ussd.UssdDTO
import com.example.seerbitsdk.navigateSingleTopNoSavedState
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.TransactionViewModel

@Composable
fun USSDSelectBanksScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState?,
   transactionViewModel : TransactionViewModel
) {
    var bankCode by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showCircularProgressBar by remember { mutableStateOf(false) }
    var paymentRef by  remember { mutableStateOf("") }
    var ussdCode by  remember { mutableStateOf("") }
    var amount : String = "20" //todo this would later be parsed from the developer
    var defaultCurrency : String = ""

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

                defaultCurrency = merchantDetailsData.payload?.defaultCurrency?:""

                SeerbitPaymentDetailHeader(
                    charges =  merchantDetailsData.payload?.vatFee?.toDouble()?: 0.00,
                    amount = amount,
                    currencyText = defaultCurrency,
                    "Choose your bank to start this payment",
                    merchantDetailsData.payload?.businessName?:"",
                    merchantDetailsData.payload?.supportEmail?:""
                )
                Spacer(modifier = Modifier.height(41.dp))


                if (showErrorDialog) {
                    ErrorDialog(message = "Kindly Select a bank")
                }

                if(showCircularProgressBar){
                    showCircularProgress(showProgress = true)
                }

                UssdSelectBankButton {
                    bankCode = it
                }

                val ussdDTO = UssdDTO(
                    country = merchantDetailsData.payload?.country?.nameCode ?: "",
                    bankCode = bankCode,
                    amount = amount,
                    redirectUrl = "http://localhost:3002/#/",
                    productId = "",
                    mobileNumber = merchantDetailsData.payload?.number,
                    paymentReference = transactionViewModel.generateRandomReference(),
                    fee = merchantDetailsData.payload?.vatFee,
                    fullName = merchantDetailsData.payload?.businessName,
                    channelType = "ussd",
                    publicKey = "SBPUBK_TCDUH6MNIDLHMJXJEJLBO6ZU2RNUUPHI",
                    source = "",
                    paymentType = "USSD",
                    sourceIP = "102.88.63.64",
                    currency = merchantDetailsData.payload?.defaultCurrency,
                    productDescription = "",
                    email = "sdk@gmail.com",
                    retry = false,
                    ddeviceType = "Android"
                )

                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateUssdPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState2.value
                //enter payment states


                if (initiateUssdPayment.hasError) {
                    showCircularProgressBar = false
                    transactionViewModel.resetTransactionState()
                }

                if (initiateUssdPayment.isLoading) {
                    showCircularProgressBar = true
                }

                initiateUssdPayment.data?.let {
                    paymentRef = it.data?.payments?.paymentReference ?: ""
                    ussdCode = it.data?.payments?.ussdDailCode?: ""
                    Log.d("ussdCodesss", ussdCode)
                    showCircularProgressBar = false
                    navController.navigateSingleTopNoSavedState(
                        "${Route.USSD_HOME_SCREEN}/$paymentRef/${ussdCode}")

                }




                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Pay $defaultCurrency $amount",
                    onClick = {
                        if(bankCode.isNotEmpty()) {
                            transactionViewModel.initiateUssdTransaction(ussdDTO)
                        }
                        else {
                            showErrorDialog = true
                        }
                    }, !showCircularProgressBar
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
        Card(modifier = modifier, elevation = 1.dp) {

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
                    .fillMaxHeight(),
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

fun setAmountWithCurrency(){

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDSelectBankButtonPreview() {
    SeerBitTheme {
        UssdSelectBankButton {}
    }
}

