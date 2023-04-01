package com.example.seerbitsdk


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.bank.*
import com.example.seerbitsdk.card.CardEnterPinScreen
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.models.CardDetails
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.component.OtherPaymentScreen
import com.example.seerbitsdk.helper.TransactionType
import com.example.seerbitsdk.helper.displayPaymentMethod
import com.example.seerbitsdk.models.OnCloseSeerBitSdkListener
import com.example.seerbitsdk.momo.MOMOOTPScreen
import com.example.seerbitsdk.momo.MomoHomeScreen
import com.example.seerbitsdk.screenstate.*
import com.example.seerbitsdk.transfer.TransferHomeScreen
import com.example.seerbitsdk.ui.theme.*
import com.example.seerbitsdk.ussd.USSDHomeScreen
import com.example.seerbitsdk.ussd.USSDSelectBanksScreen
import com.example.seerbitsdk.viewmodels.CardEnterPinViewModel
import com.example.seerbitsdk.viewmodels.MerchantDetailsViewModel
import com.example.seerbitsdk.viewmodels.SelectBankViewModel
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.google.gson.Gson

class SeerBitActivity : ComponentActivity() {
    private val viewModel: MerchantDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val merchantDetailsViewModel: MerchantDetailsViewModel by viewModels()
            val cardEnterPinViewModel: CardEnterPinViewModel by viewModels()
            val transactionViewModel: TransactionViewModel by viewModels()
            val selectBankViewModel: SelectBankViewModel by viewModels()

            val amount = intent.extras?.getString("amount")
            val publicKey = intent.extras?.getString("publicKey")
            val phoneNumber = intent.extras?.getString("mobile_number")
            val fullName = intent.extras?.getString("fullName")
            val emailAddress = intent.extras?.getString("emailAddress")
            val paymentRef = intent.extras?.getString("paymentRef")


            val merchantDetailsState = merchantDetailsViewModel.merchantState.value

            if (merchantDetailsState.hasError) {
                ErrorExitDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
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

                SeerBitApp(
                    merchantDetailsViewModel,
                    transactionViewModel,
                    cardEnterPinViewModel,
                    selectBankViewModel,
                    amount ?: "",
                    publicKey ?: "",
                    phoneNumber ?: "",
                    fullName ?: "",
                    emailAddress ?: "",
                    paymentRef ?: ""
                )
            }
        }
    }
}


fun startSeerBitSDK(
    context: Context,
    amount: String,
    phoneNumber: String,
    publicKey: String,
    fullName: String,
    emailAddress: String,
    optionalPaymentReference: String = "",
) {

    val intent = Intent(context, SeerBitActivity::class.java)

    intent.putExtra("amount", amount)
    intent.putExtra("mobile_number", phoneNumber)
    intent.putExtra("publicKey", publicKey)
    intent.putExtra("fullName", fullName)
    intent.putExtra("emailAddress", emailAddress)
    intent.putExtra("paymentRef", optionalPaymentReference)

    context.startActivity(intent)

}


@Composable
fun SeerBitApp(
    viewModel: MerchantDetailsViewModel,
    transactionViewModel: TransactionViewModel,
    cardEnterPinViewModel: CardEnterPinViewModel,
    selectBankViewModel: SelectBankViewModel,
    amount: String,
    publicKey: String,
    phoneNumber: String,
    fullName: String,
    emailAddress: String,
    paymentRef: String
) {
    SeerBitTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 0.dp),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val bottomBarState = remember { (mutableStateOf(true)) }

            //Fetch your currentDestination
            val currentDestination = currentBackStack?.destination

            when (currentDestination?.route) {
                BankAccount.route -> bottomBarState.value = false
                "otpscreen" -> bottomBarState.value = false
                else -> bottomBarState.value = true
            }
            val currentScreen =
                rallyTabRowScreens.find { it.route == currentDestination?.route } ?: BankAccount
            val activity = LocalContext.current as Activity

            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


            viewModel.merchantState.value.data?.payload?.userFullName = fullName
            viewModel.merchantState.value.data?.payload?.userPhoneNumber = phoneNumber
            viewModel.merchantState.value.data?.payload?.publicKey = publicKey
            viewModel.merchantState.value.data?.payload?.amount = amount
            viewModel.merchantState.value.data?.payload?.emailAddress = emailAddress

            if (paymentRef.isEmpty()) {
                viewModel.merchantState.value.data?.payload?.paymentReference =
                    transactionViewModel.paymentRef.value
            } else {
                viewModel.merchantState.value.data?.payload?.paymentReference = paymentRef
            }

            viewModel.setPublicKey(publicKey)
            PUBLIC_KEY = publicKey

            MyAppNavHost(
                navController = navController,
                modifier = Modifier.padding(8.dp),
                currentDestination = currentBackStack?.destination,
                merchantDetailsState = viewModel.merchantState.value,
                transactionViewModel = transactionViewModel,
                cardEnterPinViewModel = cardEnterPinViewModel,
                selectBankViewModel = selectBankViewModel,
                merchantDetailsViewModel = viewModel,
                startDestination = Debit_CreditCard.route,
            )


        }
    }


}


fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }


fun NavHostController.navigateSingleTopNoPopUpToHome(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopNoPopUpToHome.graph.findNode(route)!!.id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateSingleTopNoSavedState(route: String) =
    this.navigate(route) {
        this@navigateSingleTopNoSavedState.graph.findNode(route)?.id?.let {
            popUpTo(it) {
                saveState = false
            }
        }
        launchSingleTop = true
        restoreState = true
    }


@Preview(showBackground = true)
@Composable
fun SeerBitWaterMarkPreview() {
    SeerBitTheme {
        BottomSeerBitWaterMark()
    }
}

@Composable
fun showCircularProgress(showProgress: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            color = Color.DarkGray,
        )
    }
}

@Composable
fun CardHomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToPinScreen: (CardDTO) -> Unit,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    onOtherPaymentButtonClicked: () -> Unit,
    transactionViewModel: TransactionViewModel,
) {

    var cardDetailsData: CardDetails by remember { mutableStateOf(CardDetails("", "", "", "")) }

    //card details
    var cvv by rememberSaveable { mutableStateOf("") }
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var cardExpiryMonth by rememberSaveable { mutableStateOf("") }
    var cardExpiryYear by rememberSaveable { mutableStateOf("") }
    var redirectUrl by rememberSaveable { mutableStateOf("") }
    var canRedirectToUrl by remember { mutableStateOf(false) }
    var trailingIcon by rememberSaveable { mutableStateOf(0) }


    //determines if to show progress bar when loading
    var showCircularProgressBar by remember { mutableStateOf(false) }
    var paymentRef by remember { mutableStateOf("") }
    var alertDialogMessage by remember { mutableStateOf("") }
    var alertDialogHeaderMessage by remember { mutableStateOf("") }
    val openDialog = remember { mutableStateOf(false) }
    val exitOnSuccess = remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)


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
        Column(
            modifier = modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp
                )
                .fillMaxWidth(),

            ) {

            var paymentReference = merchantDetailsData.payload?.paymentReference ?: ""
            val amount = merchantDetailsData.payload?.amount

            val cardDTO = CardDTO(
                deviceType = "Android",
                country = merchantDetailsData.payload?.country?.countryCode ?: "",
                amount = amount?.toDouble(),
                cvv = cvv,
                redirectUrl = "http://localhost:3002/#/",
                productId = "",
                mobileNumber = merchantDetailsData.payload?.userPhoneNumber,
                paymentReference = paymentReference,
                fee = merchantDetailsData.payload?.vatFee,
                expiryMonth = cardExpiryMonth,
                fullName = merchantDetailsData.payload?.userFullName,
                "MASTERCARD",
                publicKey = merchantDetailsData.payload?.publicKey,
                expiryYear = cardExpiryYear,
                source = "",
                paymentType = "CARD",
                sourceIP = "0.0.0.1",
                pin = "",
                currency = merchantDetailsData.payload?.defaultCurrency,
                "LOCAL",
                false,
                email = merchantDetailsData.payload?.emailAddress,
                cardNumber = cardNumber,
                retry = transactionViewModel.retry.value
            )
            Spacer(modifier = Modifier.height(25.dp))

            //SeerBit Header
            SeerbitPaymentDetailHeader(
                charges = merchantDetailsData.payload?.vatFee?.toDouble() ?: 0.0,
                amount = amount ?: "",
                currencyText = merchantDetailsData.payload?.defaultCurrency ?: "",
                "Debit/Credit Card Details",
                merchantDetailsData.payload?.businessName ?: "",
                merchantDetailsData.payload?.supportEmail ?: ""
            )

            Spacer(modifier = Modifier.height(8.dp))


            val cardBinState: CardBinState =
                transactionViewModel.cardBinState.value


            if (cardBinState.hasError) {
                openDialog.value = true
                alertDialogMessage = "Invalid Card Details"
                alertDialogHeaderMessage = "Error Occurred"
                transactionViewModel.clearCardBinState()
            }
            if (cardBinState.isLoading) {

            }


            //Card Details Screen
            CardDetailsScreen(
                cardNumber = cardDetailsData.cardNumber,
                onChangeCardCvv = {
                    cvv = it
                }, onChangeCardExpiryMonth = {
                    cardExpiryMonth = it
                },
                onChangeCardExpiryYear = {
                    cardExpiryYear = it
                },
                onChangeCardNumber = {
                    cardNumber = it
                    if (it.length >= 16 || it.length >= 6) {

                        transactionViewModel.fetchCardBin(it)

                        if (cardBinState.data != null) {
                            var split: List<String?>
                            if (cardBinState.data.responseMessage != "BIN not Found") {
                                transactionViewModel.clearCardBinState()
                                split = cardBinState.data.cardName?.split(" ")!!

                                trailingIcon = if (split[0].equals("MASTERCARD")) {
                                    R.drawable.mastercard

                                } else if (split[0].equals("VISA")) {
                                    R.drawable.visa
                                } else if (split[0].equals("Interswitch", ignoreCase = true)) {
                                    0
                                } else if (split[0].equals("VERVE")) {
                                    R.drawable.verve_logo
                                } else 0
                            } else {
                                trailingIcon = 0

                            }

                        }
                    } else if (it.length < 6) {
                        transactionViewModel.clearCardBinState()
                        trailingIcon = 0
                    }


                }, trailingIcon = trailingIcon
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showCircularProgressBar) {
                showCircularProgress(showProgress = true)
            }

            Spacer(modifier = Modifier.height(16.dp))
            //Payment Button Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PayButton(
                    amount = "NGN ${cardDTO.amount}",
                    onClick = {

                        if (validateCardDetails(
                                cvv.isValidCvv(),
                                cardNumber.isValidCardNumber(),
                                cardExpiryMonth.isValidCardExpiryMonth()
                            )
                        ) {
                            onNavigateToPinScreen(cardDTO)
                            transactionViewModel.setRetry(true)

                        } else {
                            openDialog.value = true
                            alertDialogMessage = "Invalid Card Details"
                            alertDialogHeaderMessage = "Error Occurred"
                        }
                    }, !showCircularProgressBar
                )
            }


            val transactionState: InitiateTransactionState =
                transactionViewModel.initiateTransactionState.value

            //HANDLES initiate query response
            val queryTransactionStateState: QueryTransactionState =
                transactionViewModel.queryTransactionState.value
            //querying transaction happens after otp has been inputted

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


            if (queryTransactionStateState.hasError) {
                openDialog.value = true
                alertDialogMessage = "Error"
                alertDialogHeaderMessage =
                    queryTransactionStateState.errorMessage ?: "Something went wrong"
                transactionViewModel.resetTransactionState()
                showCircularProgressBar = false

            }
            if (queryTransactionStateState.isLoading) {

            }

            queryTransactionStateState.data?.data?.let {

                when (it.code) {
                    PENDING_CODE -> {
                        transactionViewModel.queryTransaction(paymentRef)
                    }
                    SUCCESS -> {
                        showCircularProgressBar = false
                        openDialog.value = true
                        alertDialogHeaderMessage = "Successful"
                        alertDialogMessage = it.message ?: ""
                        exitOnSuccess.value = true
                        transactionViewModel.resetTransactionState()
                        return@let
                    }
                    else -> {
                        showCircularProgressBar = false
                        openDialog.value = true
                        alertDialogHeaderMessage = "Error"
                        alertDialogMessage = it.message ?: "Something went wrong"
                        transactionViewModel.resetTransactionState()
                        return@let
                    }

                }
            }



            transactionState.data?.let {

                if (it.data?.code == SUCCESS || it.data?.code == PENDING_CODE) {
                    showCircularProgressBar = false
                    transactionViewModel.setRetry(true)
                    paymentRef = transactionState.data.data?.payments?.paymentReference ?: ""
                    val linkingRef = transactionState.data.data?.payments?.linkingReference ?: ""
                    val toEnterPinScreen = transactionState.data.data?.message == KINDLY_ENTER_PIN
                    val useOtp =
                        transactionState.data.data?.message?.contains(KINDLY_ENTER_THE_OTP, true)
                            ?: false
                    val otpText = transactionState.data.data?.message
                    transactionState.data.data?.payments?.redirectUrl?.let {
                        redirectUrl = it
                        canRedirectToUrl = true
                    }
                    if (toEnterPinScreen || useOtp) {
                        redirectUrl = ""
                        navController.navigateSingleTopTo(
                            "${Route.PIN_SCREEN}/$paymentRef/$cvv/$cardNumber/$cardExpiryMonth/$cardExpiryYear/$toEnterPinScreen/$useOtp/$otpText/$linkingRef"
                        )
                        return@let
                    } else if (canRedirectToUrl) {
                        redirectUrl(redirectUrl = redirectUrl)
                        transactionViewModel.queryTransaction(paymentRef)
                        transactionViewModel.resetTransactionState()
                        canRedirectToUrl = false
                        showCircularProgressBar = true
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
            Spacer(modifier = Modifier.height(100.dp))

            OtherPaymentButtonComponent(
                onOtherPaymentButtonClicked = onOtherPaymentButtonClicked,
                onCancelButtonClicked = { navController.navigateSingleTopTo(Debit_CreditCard.route) },
                enable = !showCircularProgressBar
            )

            Spacer(modifier = Modifier.height(100.dp))
            BottomSeerBitWaterMark(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))


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
                            Text(
                                text = alertDialogHeaderMessage,
                                style = TextStyle(),
                                textAlign = TextAlign.Center
                            )
                        }

                    },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = alertDialogMessage,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                                if (exitOnSuccess.value) {
                                    activity?.finish()
                                }

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


@Composable
fun redirectUrl(context: Context = LocalContext.current, redirectUrl: String) {
    val urlIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(redirectUrl)
    )
    LocalContext.current.startActivity(urlIntent)
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    val viewModel: MerchantDetailsViewModel = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()
    val cardEnterPinViewModel: CardEnterPinViewModel = viewModel()
    val selectBankViewModel: SelectBankViewModel = viewModel()
    SeerBitApp(
        viewModel,
        transactionViewModel,
        cardEnterPinViewModel,
        selectBankViewModel,
        publicKey = "",
        phoneNumber = "",
        fullName = "",
        amount = "",
        emailAddress = "",
        paymentRef = ""
    )
}


@Composable
fun CardDetailsScreen(
    modifier: Modifier = Modifier,
    cardNumber: String,
    onChangeCardNumber: (String) -> Unit,
    onChangeCardExpiryMonth: (String) -> Unit,
    onChangeCardExpiryYear: (String) -> Unit,
    onChangeCardCvv: (String) -> Unit,
    trailingIcon: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Card(
            modifier = modifier,
            elevation = 1.dp,
            border = BorderStroke(0.5.dp, Color.LightGray)
        ) {
            var value by rememberSaveable { mutableStateOf(cardNumber) }

            OutlinedTextField(
                value = value,
                visualTransformation = { cardNumberFormatting(it) },
                onValueChange = { newText ->
                    if (newText.length <= 20) {
                        value = newText
                        onChangeCardNumber(newText)
                    }


                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Send
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                trailingIcon = {
                    if (trailingIcon != 0) {
                        Image(
                            painter = painterResource(id = trailingIcon),
                            contentDescription = ""
                        )
                    } else CircularProgressIndicator(
                        Modifier.size(15.dp),
                        strokeWidth = 1.dp,
                        color = Color.Black
                    )
                },

                shape = RoundedCornerShape(4.dp),
                placeholder = {
                    Text(
                        text = "Card Number",
                        style = TextStyle(fontSize = 12.sp, fontFamily = Faktpro),
                        color = Color.Black
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
        }

        Spacer(modifier = modifier.height(16.dp))

        //MMM_CVVScreen(modifier = , cardDetails = )
        Row(modifier = Modifier) {

            Card(
                modifier = modifier.weight(1f),
                elevation = 1.dp,
                border = BorderStroke(0.5.dp, Color.LightGray)
            ) {
                var value by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(
                    value = value,
                    visualTransformation = { cardExpiryDateFilter(it) },
                    onValueChange = { newText ->
                        if (newText.length <= 4) {
                            value = newText
                            if (newText.length <= 2) {
                                onChangeCardExpiryMonth(newText)
                            }
                            if (newText.length >= 2) {
                                onChangeCardExpiryYear(newText.substring(2))
                            }
                        }

                    }, keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Send
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        disabledIndicatorColor = Color.Transparent,
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Gray

                    ),
                    shape = RoundedCornerShape(4.dp),
                    placeholder = {
                        Text(
                            text = "MM/YY",
                            style = TextStyle(fontSize = 12.sp, fontFamily = Faktpro),
                            color = Color.Black
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Card(
                modifier = modifier.weight(1f),
                elevation = 1.dp,
                border = BorderStroke(0.5.dp, Color.LightGray)
            ) {
                var value by rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.length <= 3) {
                            value = newText
                            onChangeCardCvv(newText)
                        }
                    }, keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Send
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        disabledIndicatorColor = Color.Transparent,
                        disabledLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Gray

                    ),
                    shape = RoundedCornerShape(4.dp),
                    placeholder = {
                        Text(
                            text = "CVV",
                            style = TextStyle(fontSize = 12.sp, fontFamily = Faktpro),
                            color = Color.Black
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }


        }

    }

}


fun cardExpiryDateFilter(text: AnnotatedString): TransformedText {

    // change the length
    val annotatedString = AnnotatedString.Builder().run {
        for (i in text.indices) {
            append(text[i])
            if (i == 1) {
                append("/")
            }
        }
        toAnnotatedString()
    }


    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset + 1
            return 5
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset - 1
            return 4
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


fun cardNumberFormatting(text: AnnotatedString): TransformedText {

    // change the length
    val annotatedString = AnnotatedString.Builder().run {
        for (i in text.indices) {
            append(text[i])
            if (i == 3 || i == 7 || i == 11 || i == 15) {
                append(" ")
            }
        }
        toAnnotatedString()
    }


    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset + 1
            if (offset <= 11) return offset + 2
            if (offset <= 15) return offset + 3
            if (offset <= 19) return offset + 4
            return 24
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset - 1
            if (offset <= 11) return offset - 2
            if (offset <= 15) return offset - 3
            if (offset <= 19) return offset - 4
            return 20
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


@Preview(showBackground = true, widthDp = 518)
@Composable
fun CardDetailsPreview() {
    SeerBitTheme {
        CardDetailsScreen(
            cardNumber = "",
            onChangeCardNumber = {},
            onChangeCardExpiryMonth = {},
            onChangeCardExpiryYear = {},
            onChangeCardCvv = {},
            trailingIcon = 0
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentOptionButtons(
    paymentName: String,
    paymentDescription: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit
) {
    Surface(

        shape = RoundedCornerShape(8.dp),

        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(4.dp)
            .clickable { onCardClick() },
        color = LighterGray,

        ) {

        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = paymentName)
            Text(text = paymentDescription)
        }
    }

}


@Composable
fun PayButton(
    amount: String,
    onClick: () -> Unit, enabled: Boolean
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()

    ) {
        Text(
            text = "Pay $amount",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp
            )
        )
    }

}


@Preview(showBackground = true, widthDp = 320)
@Composable
fun payViaComponentPreview() {
    SeerBitTheme {
        PayViaComponent()
    }
}

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Debit_CreditCard.route,
    currentDestination: NavDestination?,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    cardEnterPinViewModel: CardEnterPinViewModel,
    selectBankViewModel: SelectBankViewModel,
    merchantDetailsViewModel: MerchantDetailsViewModel,
) {
    var mStartDestination: String = Debit_CreditCard.route
    if (!displayPaymentMethod(TransactionType.CARD.type, merchantDetailsState.data))
        mStartDestination = Route.OTHER_PAYMENT_SCREEN



    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = mStartDestination
    ) {

        composable(Route.OTHER_PAYMENT_SCREEN) {
            OtherPaymentScreen(
                onCancelButtonClicked = { navController.navigateSingleTopTo(Debit_CreditCard.route) },
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel
            )
        }

        composable(
            route = Debit_CreditCard.route,
            deepLinks = listOf(navDeepLink {
                this.uriPattern = "http://localhost:3002/#/"
                this.action = Intent.ACTION_VIEW
            })

        ) {
            transactionViewModel.resetTransactionState()
            CardHomeScreen(
                onNavigateToPinScreen = { cardDTO ->
                    // if there is an error loading the report
                    transactionViewModel.initiateTransaction(cardDTO)
                },
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                onOtherPaymentButtonClicked = { navController.navigateSingleTopTo(Route.OTHER_PAYMENT_SCREEN) },
                transactionViewModel = transactionViewModel
            )
        }

        composable(

            "${Route.PIN_SCREEN}/{paymentRef}/{cvv}/{cardNumber}/{cardExpiryMonth}/{cardExpiryYear}/{isEnterPin}/{useOtp}/{otpText}/{linkingRef}",
            arguments = listOf(
                // declaring argument type
                navArgument("paymentRef") { type = NavType.StringType },
                navArgument("cvv") { type = NavType.StringType },
                navArgument("cardNumber") { type = NavType.StringType },
                navArgument("cardExpiryMonth") { type = NavType.StringType },
                navArgument("cardExpiryYear") { type = NavType.StringType },
                navArgument("isEnterPin") { type = NavType.BoolType },
                navArgument("useOtp") { type = NavType.BoolType },
                navArgument("otpText") { type = NavType.StringType },
                navArgument("linkingRef") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->
            val paymentReference = navBackStackEntry.arguments?.getString("paymentRef")
            val cvv = navBackStackEntry.arguments?.getString("cvv")
            val cardNumber = navBackStackEntry.arguments?.getString("cardNumber")
            val cardExpiryMonth = navBackStackEntry.arguments?.getString("cardExpiryMonth")
            val cardExpiryYear = navBackStackEntry.arguments?.getString("cardExpiryYear")
            val isEnterPin = navBackStackEntry.arguments?.getBoolean("isEnterPin")
            val useOtp = navBackStackEntry.arguments?.getBoolean("useOtp")
            val otpText = navBackStackEntry.arguments?.getString("otpText")
            val linkingRef = navBackStackEntry.arguments?.getString("linkingRef")

            cardEnterPinViewModel.resetTransactionState()
            CardEnterPinScreen(
                onPayButtonClicked = { cardDTO ->
                    cardEnterPinViewModel.initiateTransaction(cardDTO)
                },
                currentDestination = currentDestination,
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                cardEnterPinViewModel = cardEnterPinViewModel,
                onOtherPaymentButtonClicked = { navController.navigateSingleTopTo(Route.OTHER_PAYMENT_SCREEN) },
                paymentReference = paymentReference!!,
                cvv = cvv!!,
                cardNumber = cardNumber!!,
                cardExpiryMonth = cardExpiryMonth!!,
                cardExpiryYear = cardExpiryYear!!,
                isEnterPin = isEnterPin!!,
                useOtp = useOtp!!,
                otpText = otpText!!,
                linkingRef = linkingRef!!
            )
        }

        //BANK ACCOUUT HOME SCREEN
        composable(route = BankAccount.route) {
            transactionViewModel.resetTransactionState()
            BankAccountSelectBankScreen(
                navigateToUssdHomeScreen = { /*TODO*/ },
                currentDestination = currentDestination,
                navController = navController,
                transactionViewModel = transactionViewModel,
                merchantDetailsState = merchantDetailsState,
                selectBankViewModel = selectBankViewModel
            )
        }

        //BANK ACCOUNT NUMBER SCREEN
        composable(
            "${Route.BANK_ACCOUNT_NUMBER_SCREEN}/{bankName}/{requiredFields}/{bankCode}",
            arguments = listOf(
                // declaring argument type
                navArgument("requiredFields") { type = NavType.StringType },
                navArgument("bankCode") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->
            val requiredFieldsJson: String? =
                navBackStackEntry.arguments?.getString("requiredFields")
            val requiredField =
                Gson().fromJson(requiredFieldsJson.toString(), RequiredFields::class.java)

            val bankCode = navBackStackEntry.arguments?.getString("bankCode")
            val bankName = navBackStackEntry.arguments?.getString("bankName")

            BankAccountNumberScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                bankCode = bankCode,
                requiredFields = requiredField,
                bankName = bankName
            )
        }


        //BANK ACCOUNT BVN SCREEN
        composable(
            "${Route.BANK_ACCOUNT_BVN_SCREEN}/{bankName}/{requiredFields1}/{bankCode}/{accountNumber}",
            arguments = listOf(
                // declaring argument type
                navArgument("requiredFields1") { type = NavType.StringType },
                navArgument("bankCode") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },
                navArgument("accountNumber") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->
            // getJsonField
            val requiredFieldsJson: String? =
                navBackStackEntry.arguments?.getString("requiredFields1")
            val requiredField =
                Gson().fromJson(requiredFieldsJson.toString(), RequiredFields::class.java)

            val bankCode = navBackStackEntry.arguments?.getString("bankCode")
            val bankName = navBackStackEntry.arguments?.getString("bankName")
            val accountNumber = navBackStackEntry.arguments?.getString("accountNumber")



            BankAccountBVNScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                bankCode = bankCode,
                bankAccountNumber = accountNumber!!,
                requiredFields = requiredField,
                bankName = bankName

            )
        }

        //BANK ACCOUNT DOB SCREEN
        composable(
            "${Route.BANK_ACCOUNT_DOB_SCREEN}/{bankName}/{requiredFields2}/{bankCode}/{accountNumber}/{bvn}",
            arguments = listOf(
                // declaring argument type
                navArgument("requiredFields2") { type = NavType.StringType },
                navArgument("bankCode") { type = NavType.StringType },
                navArgument("accountNumber") { type = NavType.StringType },
                navArgument("bvn") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },
            )
        ) { navBackStackEntry ->
            // getJsonField
            val requiredFieldsJson: String? =
                navBackStackEntry.arguments?.getString("requiredFields2")
            val requiredField =
                Gson().fromJson(requiredFieldsJson.toString(), RequiredFields::class.java)

            val bankCode = navBackStackEntry.arguments?.getString("bankCode")
            val accountNumber = navBackStackEntry.arguments?.getString("accountNumber")
            val bvn = navBackStackEntry.arguments?.getString("bvn")
            val bankName = navBackStackEntry.arguments?.getString("bankName")


            BankAccountDOBScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                bankCode = bankCode!!,
                bankAccountNumber = accountNumber!!,
                bvn = bvn!!,
                requiredFields = requiredField,
                bankName = bankName
            )
        }

        //BANK ACCOUNT OTP SCREEN
        composable(
            "${Route.BANK_ACCOUNT_OTP_SCREEN}/{bankName}/{requiredFields3}/{bankCode}/{accountNumber}/{bvn}/{birthday}/{linkingReference}",
            arguments = listOf(
                // declaring argument type
                navArgument("requiredFields3") { type = NavType.StringType },
                navArgument("bankCode") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },
                navArgument("accountNumber") { type = NavType.StringType },
                navArgument("bvn") { type = NavType.StringType },
                navArgument("birthday") { type = NavType.StringType },
                navArgument("linkingReference") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->
            // getJsonField
            val requiredFieldsJson: String? =
                navBackStackEntry.arguments?.getString("requiredFields3")
            val requiredField =
                Gson().fromJson(requiredFieldsJson.toString(), RequiredFields::class.java)

            val bankCode = navBackStackEntry.arguments?.getString("bankCode")
            val bankName = navBackStackEntry.arguments?.getString("bankName")
            val accountNumber = navBackStackEntry.arguments?.getString("accountNumber")
            val bvn = navBackStackEntry.arguments?.getString("bvn")
            val birthday = navBackStackEntry.arguments?.getString("birthday")
            val linkingReference = navBackStackEntry.arguments?.getString("linkingReference")
            transactionViewModel.resetTransactionState()
            BankAccountOTPScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                bankCode = bankCode,
                bankAccountNumber = accountNumber!!,
                dob = birthday!!,
                bvn = bvn!!,
                requiredFields = requiredField,
                bankName = bankName,
                linkingReference = linkingReference
            )
        }

        //BANK ACCOUNT redirect url SCREEN
        composable(
            "${Route.BANK_ACCOUNT_REDIRECT_URL_SCREEN}/{bankName}/{bankCode}",
            arguments = listOf(
                // declaring argument type
                navArgument("bankCode") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->
            val bankCode = navBackStackEntry.arguments?.getString("bankCode")
            val bankName = navBackStackEntry.arguments?.getString("bankName")

            BankRedirectUrlScreen(
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                bankCode = bankCode!!,
                bankName = bankName!!
            )
        }


        composable(
            route = "${Transfer.route}/{paymentRef}/{wallet}/{walletName}/{bankName}/{accountNumber}",

            arguments = listOf(
                navArgument("paymentRef") { type = NavType.StringType },
                navArgument("wallet") { type = NavType.StringType },
                navArgument("walletName") { type = NavType.StringType },
                navArgument("bankName") { type = NavType.StringType },
                navArgument("accountNumber") { type = NavType.StringType },

                )
        )
        { navBackStackEntry ->
            val paymentRef = navBackStackEntry.arguments?.getString("paymentRef")
            val wallet = navBackStackEntry.arguments?.getString("wallet")
            val walletName = navBackStackEntry.arguments?.getString("walletName")
            val bankName = navBackStackEntry.arguments?.getString("bankName")
            val accountNumber = navBackStackEntry.arguments?.getString("accountNumber")

            transactionViewModel.resetTransactionState()
            TransferHomeScreen(
                merchantDetailsState = merchantDetailsState,
                paymentRef = paymentRef,
                navController = navController,
                walletName = walletName,
                bankName = bankName,
                accountNumber = accountNumber,

                )
        }

        composable(route = PhoneNumber.route) {

        }

        composable(
            "${Route.USSD_HOME_SCREEN}/{paymentReference}/{ussdCode}",
            arguments = listOf(
                // declaring argument type
                navArgument("ussdCode") { type = NavType.StringType },
                navArgument("paymentReference") { type = NavType.StringType },

                )
        ) { navBackStackEntry ->

            val ussdCode = navBackStackEntry.arguments?.getString("ussdCode")
            val paymentReference = navBackStackEntry.arguments?.getString("paymentReference")
            transactionViewModel.resetTransactionState()
            USSDHomeScreen(
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                ussdCode = "$ussdCode#",
                paymentReference = paymentReference,
                navController = navController
            )
        }


        composable(UssdSelectBank.route) {
            USSDSelectBanksScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel
            )

        }

        composable(MOMO.route) {
            MomoHomeScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                merchantDetailsState = merchantDetailsState,
                selectBankViewModel = selectBankViewModel
            )

        }

        composable(
            "${Route.MOMO_OTP_SCREEN}/{linkingReference}",
            arguments = listOf(
                // declaring argument type
                navArgument("linkingReference") { type = NavType.StringType })
        ) { navBackStackEntry ->

            val linkingReference = navBackStackEntry.arguments?.getString("linkingReference")
            transactionViewModel.resetTransactionState()
            MOMOOTPScreen(
                navController = navController,
                merchantDetailsState = merchantDetailsState,
                transactionViewModel = transactionViewModel,
                linkingReference = linkingReference
            )

        }

    }
}


@Composable
fun ErrorDialog(message: String) {
    val openDialog = remember { mutableStateOf(true) }
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
                    Text(text = stringResource(R.string.problem_occurred))
                }

            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(message)
                }
            },
            confirmButton = {
                Button(
                    onClick = { openDialog.value = false }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = SignalRed
                    )
                ) {
                    Text(text = "Close")

                }
            },
        )
    }
}

@Composable
fun ErrorExitDialog(context:Context = LocalContext.current, message: String) {
    val activity = context as? Activity
    val openDialog = remember { mutableStateOf(true) }
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
                    Text(text = "Error")
                }

            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(message)
                }
            },
            confirmButton = {
                Button(

                    onClick = {

                        openDialog.value = false
                        activity?.finish()

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







