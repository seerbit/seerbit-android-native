package com.example.seerbitsdk.momo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.generateSourceIp
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.momo.MomoDTO
import com.example.seerbitsdk.models.momo.MomoNetworkResponseItem
import com.example.seerbitsdk.navigateSingleTopNoSavedState
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ui.theme.SignalRed
import com.example.seerbitsdk.viewmodels.SelectBankViewModel
import com.example.seerbitsdk.viewmodels.TransactionViewModel


@Composable
fun MomoHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    merchantDetailsState: MerchantDetailsState?,
    selectBankViewModel: SelectBankViewModel
) {
    var momoNetwork by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

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

                var amount= merchantDetailsData.payload?.amount
                val fee =   calculateTransactionFee(merchantDetailsData, TransactionType.MOMO.type, amount = amount?.toDouble()?: 0.0)
                var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }

                if(isMerchantFeeBearer(merchantDetailsData)){
                    totalAmount =amount?.toDouble()
                }

                SeerbitPaymentDetailHeader(
                    charges = fee?.toDouble() ?: 0.0,
                    amount = amount?: "",
                    currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                    "Choose your bank to start this payment",
                    merchantDetailsData.payload?.userFullName ?: "",
                    merchantDetailsData.payload?.emailAddress ?: ""
                )
                Spacer(modifier = Modifier.height(10.dp))
                val paymentRef = merchantDetailsData.payload?.paymentReference ?: ""
                val momoDTO: MomoDTO =
                    MomoDTO(
                        deviceType = "Android",
                        country = merchantDetailsData.payload?.country?.countryCode ?: "",
                        amount = totalAmount.toString(),
                        productId = "",
                        redirectUrl = "http://localhost:3002/#/",
                        mobileNumber = accountNumber,
                        paymentReference = paymentRef,
                        fee = fee,
                        fullName = merchantDetailsData.payload?.userFullName,
                        channelType = "wallet",
                        publicKey = merchantDetailsData.payload?.publicKey,
                        source = "",
                        paymentType = "MOMO",
                        sourceIP = generateSourceIp(true),
                        currency = merchantDetailsData.payload?.defaultCurrency,
                        productDescription = "",
                        email = merchantDetailsData.payload?.emailAddress,
                        retry = transactionViewModel.retry.value,
                        network = momoNetwork,
                        voucherCode = ""
                    )


                if (showCircularProgressBar) {
                    showCircularProgress(showProgress = true)
                }
                val momoNetworkState = selectBankViewModel.momoNetworkState.value

                if (momoNetworkState.hasError) {
                    showCircularProgressBar = false
                }

                if (momoNetworkState.isLoading) {
                    showCircularProgressBar = true
                }

                var momoNetworkList: List<MomoNetworkResponseItem?>? = listOf()
                momoNetworkState.data?.let {
                    showCircularProgressBar = false
                    momoNetworkList = it
                }


                val initiateMomoPayment: InitiateTransactionState =
                    transactionViewModel.initiateTransactionState.value
                //enter payment states
                //HANDLES initiate query response
                val queryTransactionStateState: QueryTransactionState =
                    transactionViewModel.queryTransactionState.value

                showCircularProgressBar = initiateMomoPayment.isLoading

                //enter payment states
                if (initiateMomoPayment.hasError) {
                    showCircularProgressBar = false
                    openDialog.value = true
                    alertDialogMessage =
                        initiateMomoPayment.errorMessage ?: "Something went wrong"
                    alertDialogHeaderMessage = "Failed"
                    transactionViewModel.resetTransactionState()
                }


                initiateMomoPayment.data?.let {
                    showCircularProgressBar = true
                    transactionViewModel.setRetry(true)
                    val linkingReference = it.data?.payments?.linkingReference ?: ""
                    if (initiateMomoPayment.data.data?.code == PENDING_CODE) {

                        val paymentReference = it.data?.payments?.paymentReference ?: ""

                        if (queryTransactionStateState.data != null) {

                            when (queryTransactionStateState.data.data?.code) {
                                SUCCESS -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.data.data.payments?.reason ?: ""
                                    alertDialogHeaderMessage = "Success"
                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                                PENDING_CODE -> {
                                    transactionViewModel.queryTransaction(paymentReference)
                                }
                                else -> {
                                    showCircularProgressBar = false
                                    openDialog.value = true
                                    alertDialogMessage =
                                        queryTransactionStateState.errorMessage
                                            ?: "Something went wrong"
                                    alertDialogHeaderMessage = "Failed"
                                    transactionViewModel.resetTransactionState()
                                    return@let
                                }
                            }

                        } else transactionViewModel.queryTransaction(paymentReference)


                    } else if (initiateMomoPayment.data.data?.code == "INP") {
                        showCircularProgressBar = false
                        navController.navigateSingleTopNoSavedState(
                            "${Route.MOMO_OTP_SCREEN}/$linkingReference"
                        )
                    } else {
                        showCircularProgressBar = false
                        openDialog.value = true
                        alertDialogMessage =
                            initiateMomoPayment.data.data?.message ?: "Something went wrong"
                        alertDialogHeaderMessage = "Failed"
                        transactionViewModel.resetTransactionState()
                        return@let
                    }
                }



                MomoInputAccountNumberField(Modifier, "0 500 000 000") {
                    accountNumber = it
                }
                Spacer(modifier = modifier.height(20.dp))

                SelectProviderButton(momoNetworkList = momoNetworkList) {
                    it?.let {
                        momoNetwork = it.networks ?: ""
                    }

                }

                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Continue",
                    onClick = {
                        if (momoNetwork.isNotEmpty() && accountNumber.isNotEmpty()) {
                            transactionViewModel.initiateTransaction(momoDTO)
                        } else {
                            showCircularProgressBar = false
                            openDialog.value = true
                            alertDialogMessage =
                                " Invalid Details, Something went wrong"
                            alertDialogHeaderMessage = "Error Occurred"
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


@Composable
fun SelectProviderButton(
    modifier: Modifier = Modifier,
    momoNetworkList: List<MomoNetworkResponseItem?>?,
    onMomoNetworkSelected: (MomoNetworkResponseItem?) -> Unit
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
            border = BorderStroke(0.5.dp, Color.LightGray)) {

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
                        text = "Select Provider",
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
                momoNetworkList?.forEach { momoNetworks ->
                    DropdownMenuItem(onClick = {
                        onMomoNetworkSelected(momoNetworks)
                        selectedText = momoNetworks?.networks ?: ""
                        expanded = false
                    }) {
                        Text(text = momoNetworks?.networks ?: "")
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
        val transactionViewModel = TransactionViewModel()
        MomoHomeScreen(
            navController = rememberNavController(),
            transactionViewModel = transactionViewModel,
            merchantDetailsState = null,
            selectBankViewModel = selectBankViewModel
        )
    }
}


@Composable
fun MomoInputAccountNumberField(
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



