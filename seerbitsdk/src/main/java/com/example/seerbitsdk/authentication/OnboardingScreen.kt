package com.example.seerbitsdk.authentication

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.calculateTransactionFee
import com.example.seerbitsdk.helper.generateSourceIp
import com.example.seerbitsdk.helper.isMerchantFeeBearer
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.ModalDialog
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import kotlinx.coroutines.launch




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onPayButtonClicked: (CardDTO) -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    onOtherPaymentButtonClicked: () -> Unit,
    cvv: String,
    cardNumber: String,
    cardExpiryMonth: String,
    cardExpiryYear: String,
    transactionViewModel: TransactionViewModel
) {

    if (merchantDetailsState.hasError) {
        ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
    }

    if (merchantDetailsState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Black)
        }

    }

    merchantDetailsState.data?.let { merchantDetailsData ->

        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        var phonenumber by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var state  by remember { mutableStateOf("") }
        var postalCode by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }



        var redirectUrl by rememberSaveable { mutableStateOf("") }
        var canRedirectToUrl by remember { mutableStateOf(false) }



        //determines if to show progress bar when loading
        var showCircularProgressBar by remember { mutableStateOf(false) }
        var alertDialogMessage by remember { mutableStateOf("") }
        var alertDialogHeaderMessage by remember { mutableStateOf("") }
        val openDialog = remember { mutableStateOf(false) }
        val exitOnSuccess = remember { mutableStateOf(false) }


        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxSize().padding(8.dp)
                .verticalScroll(rememberScrollState())
                .bringIntoViewRequester(bringIntoViewRequester),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var paymentRef = merchantDetailsData.payload?.paymentReference ?: ""

            val amount = merchantDetailsData.payload?.amount
            val fee = calculateTransactionFee(
                merchantDetailsData,
                TransactionType.CARD.type,
                amount = amount?.toDouble() ?: 0.0
            )
            var totalAmount = fee?.toDouble()?.let { amount?.toDouble()?.plus(it) }
            val defaultCurrency = merchantDetailsData.payload?.defaultCurrency ?: ""

            if (isMerchantFeeBearer(merchantDetailsData)) {
                totalAmount = amount?.toDouble()
            }

            val cardDTO = CardDTO(
                deviceType = "Android",
                country = merchantDetailsData.payload?.country?.countryCode ?: "",
                amount = totalAmount,
                cvv = cvv,
                redirectUrl = "https://com.example.seerbit_sdk",
                productId = merchantDetailsData.payload?.productId,
                mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                paymentReference = paymentRef,
                fee = fee,
                expiryMonth = cardExpiryMonth,
                fullName = merchantDetailsData.payload?.userFullName,
                "MASTERCARD",
                publicKey = merchantDetailsData.payload?.publicKey,
                expiryYear = cardExpiryYear,
                source = "",
                paymentType = "CARD",
                sourceIP = generateSourceIp(useIPv4 = true),
                pin = "",
                currency = merchantDetailsData.payload?.defaultCurrency,
                isCardInternational =  "INTERNATIONAL",
                false,
                email = merchantDetailsData.payload?.emailAddress,
                cardNumber = cardNumber,
                retry = transactionViewModel.retry.value,

                tokenize = merchantDetailsData.payload?.tokenize,
                pocketReference = merchantDetailsData.payload?.pocketReference,
                productDescription = merchantDetailsData.payload?.productDescription,
                vendorId = merchantDetailsData.payload?.vendorId,
                address = address,
                city  = city,
                state =  state,
                postalCode = postalCode,
                billingCountry = country
            )




            Spacer(modifier = Modifier.height(16.dp))

            ModalDialog(
                showDialog = openDialog,
                alertDialogHeaderMessage = alertDialogHeaderMessage,
                alertDialogMessage = alertDialogMessage,
                exitOnSuccess = exitOnSuccess.value,
                onSuccess = {}
            ) {
                openDialog.value = false
            }

            //SeerBit Header
            SeerbitPaymentDetailHeaderTwo(
                charges = fee?.toDouble() ?: 0.0,
                amount = amount ?: "",
                currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                merchantDetailsData.payload?.userFullName ?: "",
                merchantDetailsData.payload?.emailAddress ?: ""
            )


            if (showCircularProgressBar) {
                showCircularProgress(showProgress = true)
            }

            Text(
                text = "Address Verification",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center

                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(10.dp)
            )

            if (merchantDetailsData.payload?.userPhoneNumber?.isEmpty() == true) {
                OutlinedTextField(
                    modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp), "Phone number", KeyboardType.Text, true
                ) {
                    phonenumber = it
                    merchantDetailsData.payload.userPhoneNumber = it
                }
            }
            //Address
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                    modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(end = 0.dp), "Address", KeyboardType.Text, true
                ){
                    address = it
                }

            //Country
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                modifier =
                modifier
                    .fillMaxWidth()
                    .padding(end = 0.dp), "Country", KeyboardType.Text, true
            ){
                country = it
            }

            //City
            Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(end = 0.dp)
                       , "City", KeyboardType.Text, true
                ){
                    city = it
                }

            //State
            Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(0.dp)
                        , "State", KeyboardType.Text, true
                ){
                    state = it
                }

            //Postal Code
            Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp)
                       , "Postal Code", KeyboardType.Phone, true
                ){
                    postalCode = it
                }


            val transactionState: InitiateTransactionState =
                transactionViewModel.initiateTransactionState.value


            if (transactionState.hasError) {
                showCircularProgressBar = false
                openDialog.value = true
                alertDialogMessage = transactionState.errorMessage ?: "Something went wrong"
                alertDialogHeaderMessage = "Error Occurred"
                transactionViewModel.resetTransactionState()
            }

            if (transactionState.isLoading) {
                showCircularProgressBar = true
            }



            transactionState.data?.let {
                transactionViewModel.setRetry(true)
                if (it.data?.code == SUCCESS || it.data?.code == PENDING_CODE) {
                    showCircularProgressBar = false

                    paymentRef = transactionState.data.data?.payments?.paymentReference ?: ""
                    val linkingRef = transactionState.data.data?.payments?.linkingReference ?: "-1"
                    val toEnterPinScreen = transactionState.data.data?.message == KINDLY_ENTER_PIN
                    val useOtp =
                        transactionState.data.data?.message?.contains(KINDLY_ENTER_THE_OTP, true)
                            ?: false
                    val otpText = transactionState.data.data?.message
                    transactionState.data.data?.payments?.redirectUrl?.let {
                        redirectUrl = it
                        canRedirectToUrl = true
                    }

                    if (toEnterPinScreen) {
                        redirectUrl = ""
                        navController.navigateSingleTopTo(
                            "${Route.PIN_SCREEN}/$paymentRef/$cvv/$cardNumber/$cardExpiryMonth/$cardExpiryYear/$address/$city/$state/$postalCode$country"
                        )
                        return@let
                    } else if (useOtp) {
                        navController.navigateSingleTopTo(
                            "${Route.CARD_OTP_SCREEN}/$paymentRef/$otpText/$linkingRef"
                        )
                        return@let
                    } else if (canRedirectToUrl) {

                        navController.navigateSingleTopTo(
                            "${Route.CARD_ACCOUNT_REDIRECT_URL_SCREEN}/$paymentRef/$cvv/$cardNumber/$cardExpiryMonth/$cardExpiryYear/$address/$city/$state/$postalCode$country"
                        )
                        return@let
                    }
                } else {
                    openDialog.value = true
                    showCircularProgressBar = false
                    alertDialogHeaderMessage = "Error"
                    alertDialogMessage = it.data?.message ?: "Something went wrong"
                    transactionViewModel.resetTransactionState()
                    return@let
                }

            }

            Spacer(modifier = Modifier.height(40.dp))
            //Payment Button Login
            Row(
                modifier =modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PayButton2(
                    amount = "Continue Payment",
                    onClick = {

                        if (
                        address.isNotEmpty() &&  city.isNotEmpty()&& state.isNotEmpty() && postalCode.isNotEmpty() &&country.isNotEmpty()
                        ) {
                            transactionViewModel.initiateTransaction(cardDTO)
                        } else {
                            openDialog.value = true
                            alertDialogMessage = "Invalid input address details"
                            alertDialogHeaderMessage = "Error Occurred"
                        }
                    }, !showCircularProgressBar
                )
            }

            }
        }
    }




@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier, headerText: String,
    keyboardType: KeyboardType, showHeaderText: Boolean,
    onEnterBVN: (String) -> Unit
) {
    Column(modifier = modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()

        if (showHeaderText) {
            Text(
                text = headerText,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }



        Card(
            modifier = modifier
                .bringIntoViewRequester(bringIntoViewRequester),
            elevation = 4.dp
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
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),

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
                        text = headerText,
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .height(56.dp)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
        }
    }


}


@Composable
fun SelectCurrencyButton(modifier: Modifier = Modifier) {

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

    Column(modifier = modifier) {
        Card(
            elevation = 4.dp,
            backgroundColor = LighterGray,
            shape = RoundedCornerShape(8.dp)
        ) {

            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = LighterGray,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = "NGN",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        textFieldSize = layoutCoordinates.size.toSize()
                    },
                trailingIcon = {
                    Icon(imageVector = icon, contentDescription = null,
                        Modifier.clickable { expanded = !expanded })
                }

            )
        }
    }

    // Create a list of cities
    val banks = listOf(
        "NGN",
        "USD",
        "GBP"
    )

    DropdownMenu(
        expanded = expanded, onDismissRequest = { expanded = false },
        modifier = Modifier.width(
            with(LocalDensity.current) { textFieldSize.width.toDp() })
    ) {
        banks.forEach { label ->
            DropdownMenuItem(onClick = {
                selectedText = label
                expanded = false
            }) {
                Text(text = label)
            }
        }

    }
}



@Preview(showBackground = true, widthDp = 400)
@Composable
fun OutlinedTextfieldPreview() {
    SeerBitTheme {
        OutlinedTextField(Modifier, "First Name", KeyboardType.Text, true){}
    }
}
