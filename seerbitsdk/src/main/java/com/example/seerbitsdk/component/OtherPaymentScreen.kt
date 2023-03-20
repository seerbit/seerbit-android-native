package com.example.seerbitsdk.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.displayPaymentMethod
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
                var amount: String = merchantDetailsData.payload?.amount ?: ""

                SeerbitPaymentDetailHeader(
                    charges = merchantDetailsData.payload?.vatFee?.toDouble() ?:0.0,
                    amount = amount,
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    "Other Payment Channels",
                    merchantDetailsData.payload?.businessName?:"",
                    merchantDetailsData.payload?.supportEmail?:""
                )
                Spacer(modifier = Modifier.height(8.dp))

                val addedButtons: ArrayList<SeerBitDestination> = arrayListOf()
                if(displayPaymentMethod(TransactionType.CARD.type, merchantDetailsData))addedButtons.add(Debit_CreditCard)
                if(displayPaymentMethod(TransactionType.USSD.type, merchantDetailsData))addedButtons.add(UssdSelectBank)
                if(displayPaymentMethod(TransactionType.MOMO.type, merchantDetailsData))addedButtons.add(MOMO)
                if(displayPaymentMethod(TransactionType.TRANSFER.type, merchantDetailsData))addedButtons.add(Transfer)
                if(displayPaymentMethod(TransactionType.ACCOUNT.type, merchantDetailsData))addedButtons.add(BankAccount)


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
                        walletName = it.data.payments.walletName?:""
                        bankName = it.data.payments.bankName?:""
                        accountNumber = it.data.payments.accountNumber?:""

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
                            .width(160.dp)
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
                                textAlign = TextAlign.Center
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
    var amount: String = merchantDetailsData.payload?.amount ?: ""

    return TransferDTO(
        country = merchantDetailsData.payload?.country?.countryCode ?: "",
        bankCode = "044",
        amount = amount,
        productId = "",
        mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
        paymentReference = generateRandomReference(),
        fee = merchantDetailsData.payload?.vatFee,
        fullName = merchantDetailsData.payload?.userFullName,
        channelType = "Transfer",
        publicKey = merchantDetailsData.payload?.publicKey,
        source = "",
        paymentType = "TRANSFER",
        sourceIP = "102.88.63.64",
        currency = merchantDetailsData.payload?.defaultCurrency,
        productDescription = "",
        email = merchantDetailsData.payload?.emailAddress,
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
