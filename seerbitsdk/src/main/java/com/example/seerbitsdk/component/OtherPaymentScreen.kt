package com.example.seerbitsdk.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun OtherPaymentScreen(
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit,
    transactionViewModel: TransactionViewModel,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState?
) {
    var walletName by remember { mutableStateOf("") }
    var paymentRef by remember { mutableStateOf("") }
    var wallet by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var isSuccesfulResponse by remember { mutableStateOf(false) }
    var showCircularProgressBar by rememberSaveable { mutableStateOf(false) }

    merchantDetailsState?.data?.let { merchantDetailsData ->
        Column(
            modifier = modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
                    .fillMaxWidth()


            ) {
                Spacer(modifier = Modifier.height(25.dp))
                SeerbitPaymentDetailHeader(
                    charges = merchantDetailsData.payload?.vatFee?.toDouble()!!,
                    amount = "20.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "Other Payment Channels",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )
                Spacer(modifier = Modifier.height(8.dp))

                val addedButtons: ArrayList<SeerBitDestination> = arrayListOf()
                merchantDetailsState.data.payload?.country?.defaultPaymentOptions?.forEach {

                    if (it?.code == "CARD" && it.status == "ACTIVE") {
                        addedButtons.add(Debit_CreditCard)
                    }
                    if (it?.code == "TRANSFER" && it.status == "ACTIVE") {
                        addedButtons.add(Transfer)
                    }
                    if (it?.code == "USSD" && it.status == "ACTIVE") {
                        addedButtons.add(UssdSelectBank)
                    }
                    if (it?.code == "ACCOUNT" && it.status == "ACTIVE") {
                        addedButtons.add(BankAccount)
                    }
                    if (it?.code == "MOMO" && it.status == "ACTIVE") {
                        addedButtons.add(MOMO)
                    }
                    if (it?.code == "POS" && it.status == "ACTIVE") {
                        //addedButtons.add(MOMO)
                    }
                }
                if(showCircularProgressBar){
                    showCircularProgress(showProgress = true)
                }

                //HANDLE INITIATE TRANSACTION RESPONSE
                val initiateTransferPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState.value


                if (initiateTransferPayment.hasError) {
                    showCircularProgressBar = true
                    transactionViewModel.resetTransactionState()
                }

                if(initiateTransferPayment.isLoading){
                    showCircularProgressBar = true
                }


                initiateTransferPayment.data?.let {
                    paymentRef = it.data?.payments?.paymentReference?:""
                    showCircularProgressBar = false
                    wallet = it.data?.payments?.wallet!!
                        walletName = it.data.payments.walletName!!
                        bankName = it.data.payments.bankName!!
                        accountNumber = it.data.payments.accountNumber!!

                    navController.navigateSingleTopNoSavedState("${Transfer.route}/$paymentRef/$wallet/$walletName/$bankName/$accountNumber")

                }


                SeerBitNavButtonsColumn(
                    allButtons = addedButtons,
                    onButtonSelected = { newScreen ->
                        if(newScreen.route == Transfer.route){
                            transactionViewModel.initiateTransaction(generateTransferDTO(merchantDetailsData))
                        }
                        else
                            navController.navigateSingleTopNoSavedState(newScreen.route)


                    },
                    currentButtonSelected = Transfer,
                    !showCircularProgressBar
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(
                        onClick = onCancelButtonClicked,
                        colors = ButtonDefaults.buttonColors(backgroundColor = SignalRed),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .width(140.dp)
                            .height(50.dp)

                    ) {
                        Text(
                            text = "Cancel Payment",

                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = Faktpro,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 10.sp,
                                color = DeepRed,
                            ),
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }

                }


            }
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

fun generateTransferDTO(merchantDetailsData: MerchantDetailsResponse) : TransferDTO {

    return TransferDTO(
        country = merchantDetailsData.payload?.country?.countryCode ?: "",
        bankCode = "044",
        amount = "20.0",
        productId = "",
        mobileNumber = merchantDetailsData.payload?.number,
        paymentReference = generateRandomReference(),
        fee = merchantDetailsData.payload?.vatFee,
        fullName = merchantDetailsData.payload?.businessName,
        channelType = "Transfer",
        publicKey = "SBPUBK_TCDUH6MNIDLHMJXJEJLBO6ZU2RNUUPHI",
        source = "",
        paymentType = "TRANSFER",
        sourceIP = "102.88.63.64",
        currency = merchantDetailsData.payload?.defaultCurrency,
        productDescription = "",
        email = "sdk@gmail.com",
        retry = false,
        deviceType = "Android",
        amountControl = "FIXEDAMOUNT",
        walletDaysActive = "1"
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    OtherPaymentScreen(
        onCancelButtonClicked = { /*TODO*/ },
        navController = rememberNavController(),
        merchantDetailsState = null,
        transactionViewModel = viewModel
    )
}
