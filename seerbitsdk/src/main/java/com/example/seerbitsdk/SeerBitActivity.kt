package com.example.seerbitsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.bank.BankAccountNumberScreen
import com.example.seerbitsdk.bank.BankScreen
import com.example.seerbitsdk.card.CardEnterPinScreen
import com.example.seerbitsdk.card.OTPScreen
import com.example.seerbitsdk.component.*
import com.example.seerbitsdk.navigationpage.OtherPaymentScreen
import com.example.seerbitsdk.transfer.TransferHomeScreen
import com.example.seerbitsdk.ui.theme.*
import com.example.seerbitsdk.ussd.USSDHomeScreen

class SeerBitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeerBitApp()
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SeerBitTheme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeerBitApp() {
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

            MyAppNavHost(
                navController = navController,
                modifier = Modifier.padding(8.dp),
                currentDestination = currentBackStack?.destination
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


@Preview(showBackground = true)
@Composable
fun SeerBitWaterMarkPreview() {
    SeerBitTheme {
        BottomSeerBitWaterMark()
    }
}

@Composable
fun CardHomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToPinScreen: () -> Unit,
    onOtherPaymentButtonClicked: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController
) {


    Column(
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 8.dp
            )
            .fillMaxWidth(),

        ) {
        Spacer(modifier = Modifier.height(25.dp))
        SeerbitPaymentDetailScreen(
            charges = 0.45,
            amount = "60,000.00",
            currencyText = "NGN",
            "Debit/Credit Card Details"
        )
        Spacer(modifier = Modifier.height(8.dp))
        CardDetailsScreen()

        Spacer(modifier = Modifier.height(16.dp))

        PayButton(
            amount = "NGN 60,000",
            onClick = onNavigateToPinScreen
        )
        Spacer(modifier = Modifier.height(100.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onOtherPaymentButtonClicked,
                colors = ButtonDefaults.buttonColors(backgroundColor = LighterGray),
                shape = RoundedCornerShape(4.dp),

                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)
                    .padding(end = 8.dp)

            ) {
                Text(
                    text = "Change Payment Method",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 10.sp
                    )
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(backgroundColor = SignalRed),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)

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

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    SeerBitApp()
}

@Composable
fun CardDetailsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                visualTransformation = { cardNumberFormatting(it) },
                onValueChange = { newText -> if (newText.length <= 16) value = newText },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

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
                        text = "Card Number",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.Black
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

        Spacer(modifier = modifier.height(16.dp))
        MMM_CVVScreen(modifier = modifier)

    }
}

@Composable
fun MMM_CVVScreen(modifier: Modifier) {
    Row(modifier = Modifier) {

        Card(modifier = modifier.weight(1f), elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                visualTransformation = { cardExpiryDateFilter(it) },
                onValueChange = { newText ->
                    if (newText.length <= 4) value = newText
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                placeholder = {
                    Text(
                        text = "MM/YY",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.Black
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.vertical_divider_line),
            contentDescription = "dividing line"
        )
        //CVV Card
        Card(modifier = modifier.weight(1f), elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> if (newText.length <= 3) value = newText },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                placeholder = {
                    Text(
                        text = "CVV",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.Black
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
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
            return 5
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


fun cardNumberFormatting(text: AnnotatedString): TransformedText {

    // change the length
    val annotatedString = AnnotatedString.Builder().run {
        for (i in text.indices) {
            append(text[i])
            if (i == 3 || i == 7 || i == 11) {
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
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset - 1
            if (offset <= 11) return offset - 2
            if (offset <= 15) return offset - 3
            return 19
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


@Preview(showBackground = true, widthDp = 518)
@Composable
fun MMM_CVVScreenPreview() {
    SeerBitTheme {
        MMM_CVVScreen(modifier = Modifier)
    }
}

@Preview(showBackground = true, widthDp = 518)
@Composable
fun CardDetailsPreview() {
    SeerBitTheme {
        CardDetailsScreen()
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()

    ) {
        Text(
            text = "Pay $amount",
            style = TextStyle(
                fontSize = 14.sp,
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
    navController: NavHostController = rememberNavController(),
    startDestination: String = Debit_CreditCard.route,
    currentDestination: NavDestination?
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Route.OTHER_PAYMENT_SCREEN) {
            OtherPaymentScreen(
                onCancelButtonClicked = { navController.navigateSingleTopTo(Debit_CreditCard.route) },
                currentDestination = currentDestination,
                navController = navController
            )
        }

        composable(route = Debit_CreditCard.route) {
            CardHomeScreen(
                onNavigateToPinScreen = { navController.navigateSingleTopTo(Route.PIN_SCREEN) },
                onOtherPaymentButtonClicked = { navController.navigateSingleTopTo(Route.OTHER_PAYMENT_SCREEN) },
                currentDestination = currentDestination,
                navController = navController
            )
        }
        composable(route = Ussd.route) {
            USSDHomeScreen(
                navigateToLoadingScreen = { navController.navigateSingleTopTo(Route.PIN_SCREEN) },
                currentDestination = currentDestination,
                navController = navController
            )
        }

        composable(route = BankAccount.route) {
            BankScreen(
                onNavigateToBankAccountNumberScreen = {
                    navController.navigateSingleTopNoPopUpToHome(
                        Route.BANK_ACCOUNT_NUMBER_SCREEN
                    )
                },
                currentDestination = currentDestination,
                navController = navController
            )
        }
        composable(route = Route.BANK_ACCOUNT_NUMBER_SCREEN) {
            BankAccountNumberScreen(
                onPaymentMethodClick = {}
            )
        }


        composable(route = Transfer.route) {
            TransferHomeScreen(
                navigateToLoadingScreen = { },
                onOtherPaymentButtonClicked = { navController.navigateSingleTopTo(Route.OTHER_PAYMENT_SCREEN) },
                onCancelPaymentButtonClicked = { navController.navigateSingleTopTo(Debit_CreditCard.route) }
            )
        }

        composable(route = PhoneNumber.route) {

        }
        composable(route = Cash.route) {
            USSDHomeScreen(
                navigateToLoadingScreen = {},
                currentDestination = currentDestination,
                navController = navController
            )
        }

        composable(Route.PIN_SCREEN) {
            CardEnterPinScreen(
                onNavigateToOtpScreen = { navController.navigateSingleTopNoPopUpToHome("otpscreen") }
            )


        }
        composable("otpscreen") {
            OTPScreen(
                onPaymentMethodClick = {}
            )


        }
    }
}




