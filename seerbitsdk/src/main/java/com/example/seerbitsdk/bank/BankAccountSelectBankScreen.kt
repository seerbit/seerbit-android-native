package com.example.seerbitsdk.bank

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.BottomSeerBitWaterMark
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.Route
import com.example.seerbitsdk.component.SeerbitPaymentDetailHeader
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.formatInputDouble
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.MerchantBanksItem
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.SelectBankViewModel
import com.example.seerbitsdk.viewmodels.TransactionViewModel

@Composable
fun BankAccountSelectBankScreen(
    modifier: Modifier = Modifier,
    navigateToUssdHomeScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    merchantDetailsState: MerchantDetailsState?,
    selectBankViewModel: SelectBankViewModel
) {
    var bankCode by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }

    var json: String = ""
    var showErrorDialog by remember { mutableStateOf(false) }

    var requiredFields = RequiredFields()

    var showCircularProgressBar by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }

    transactionViewModel.resetTransactionState()
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
                val defaultCurrency =   merchantDetailsData.payload?.defaultCurrency?:""

                if(isMerchantFeeBearer(merchantDetailsData)){
                    totalAmount =amount?.toDouble()
                }

                SeerbitPaymentDetailHeader(
                    charges = fee?.toDouble()?:0.0,
                    amount = amount?:"",
                    currencyText = merchantDetailsData.payload?.defaultCurrency?:"",
                    "Choose your bank to start this payment",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )


                Spacer(modifier = Modifier.height(10.dp))

                ModalDialog(
                    showDialog = openDialog,
                    alertDialogHeaderMessage = alertDialogHeaderMessage,
                    alertDialogMessage = alertDialogMessage,
                    exitOnSuccess = false,
                    onSuccess = {}
                ) {
                    openDialog.value = false
                }

                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }
                //HANDLE AVAILABLE BANK STATE
                val availableBanksState = selectBankViewModel.availableBanksState.value

                if (availableBanksState.data == null) {
                    selectBankViewModel.getBanks()
                }

                if (availableBanksState.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogHeaderMessage = "Failed"
                    alertDialogMessage ="Error while fetching banks"
                }

                //showCircularProgressBar = availableBanksState.isLoading

                var merchantBankList: List<MerchantBanksItem?>? = listOf()
                availableBanksState.data?.availableBankData?.let {
                    showCircularProgressBar = false
                    merchantBankList = it.merchantBanks!!

                }



                BankSelectBankButton(merchantBankList = merchantBankList) {
                    requiredFields = RequiredFields()
                    it?.let {
                        requiredFields = it.requiredFields!!
                        bankCode = it.bankCode.toString()
                        bankName = it.bankName.toString()
                        json = it.requiredFieldJson()
                    }

                }

                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Pay $currency${formatInputDouble(totalAmount.toString())}",
                    onClick = {
                        if (bankCode.isNotEmpty()) {

                            if (requiredFields.accountNumber == "YES") {

                                navController.navigateSingleTopNoSavedState(
                                    "${Route.BANK_ACCOUNT_NUMBER_SCREEN}/$bankName/$json/$bankCode"
                                )

                            } else { //this means it uses web redirect url link
                                navController.navigateSingleTopNoSavedState(
                                    "${Route.BANK_ACCOUNT_REDIRECT_URL_SCREEN}/$bankName/$bankCode"
                                )
                            }
                        } else {
                            openDialog.value = true
                            alertDialogHeaderMessage = "Failed"
                            alertDialogMessage ="This action could not be completed"
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
fun BankSelectBankButton(
    modifier: Modifier = Modifier,
    merchantBankList: List<MerchantBanksItem?>?,
    onMerchantBankSelected: (MerchantBanksItem?) -> Unit
) {
    var selectedText by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldSize by remember { mutableStateOf(Size.Zero) }



    Column {
        Card(modifier = modifier, elevation = 1.dp,
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
                    .fillMaxHeight(),
                trailingIcon = {
                    Icon(imageVector = icon, contentDescription = null,
                        Modifier.clickable { expanded = !expanded })
                }

            )
        }

        Spacer(Modifier.height(10.dp))

        AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
            DropdownMenu(

                expanded = expanded, onDismissRequest = { expanded = false },
                modifier = Modifier.width(
                    with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                merchantBankList?.forEach { merchantBanksItem ->
                    DropdownMenuItem(onClick = {
                        onMerchantBankSelected(merchantBanksItem)
                        selectedText = merchantBanksItem?.bankName!!
                        expanded = false
                    }) {
                        Text(text = merchantBanksItem?.bankName!!)
                    }
                }


            }
        }


    }


}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDSelectBankButtonPreview() {
    SeerBitTheme {
        val selectBankViewModel: SelectBankViewModel = SelectBankViewModel()
        val transactionViewModel: TransactionViewModel = TransactionViewModel()
        BankAccountSelectBankScreen(
            navigateToUssdHomeScreen = { /*TODO*/ },
            currentDestination = null,
            navController = rememberNavController(),
            transactionViewModel = transactionViewModel,
            merchantDetailsState = null,
            selectBankViewModel = selectBankViewModel
        )
    }
}

