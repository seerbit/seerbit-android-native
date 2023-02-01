package com.example.seerbitsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.seerbitsdk.component.SeerBitNavButtonsColumn
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme

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
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val bottomBarState = remember { (mutableStateOf(true)) }

            //Fetch your currentDestination
            val currentDestination = currentBackStack?.destination

                when (currentDestination?.route){
                    BankAccount.route -> bottomBarState.value = false
                   "otpscreen"-> bottomBarState.value = false
                    else -> bottomBarState.value = true
                }
            val currentScreen =
                rallyTabRowScreens.find { it.route == currentDestination?.route } ?: BankAccount
            Scaffold(

                bottomBar =
                {
                    if (bottomBarState.value) {
                        SeerBitNavButtonsColumn(
                            allButtons = rallyTabRowScreens.filterNot{it.route == currentDestination?.route},
                            onButtonSelected = { newScreen ->
                                navController.navigateSingleTopTo(newScreen.route)
                            },
                            currentButtonSelected = currentScreen
                        )
                    }
                    else {}
                }

            ) { innerPadding ->
                MyAppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }

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


@Composable
fun BottomSeerBitWaterMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .width(114.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = "lock icon"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.secured_by_seerbit),
            contentDescription = "secured by seerbit"
        )

    }
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
    onNavigateToPinScreen: () -> Unit
) {

    Column(modifier = modifier) {

        Column(
            modifier = modifier
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f)
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

            Spacer(modifier = modifier.height(13.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var value by remember {
                    mutableStateOf(false)
                }
                Checkbox(
                    checked = value,
                    onCheckedChange = { newValue -> value = newValue },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = LighterGray,
                        checkedColor = Color.LightGray
                    )
                )
                Text(text = "Remember my information on this device")
            }

            Spacer(modifier = Modifier.height(8.dp))

            PayButton(
                amount = "NGN 60,000",
                onClick = onNavigateToPinScreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            PayViaComponent()
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    SeerBitApp()
}

@Composable
fun CardDetailsScreen(modifier: Modifier = Modifier) {
    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },
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
                        color = Color.LightGray
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
                onValueChange = { newText -> value = newText },
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
                        color = Color.LightGray
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
        Card(modifier = modifier.weight(1f), elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cvv_card),
                        contentDescription = null
                    )
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
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                placeholder = {
                    Text(
                        text = "CVV",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

    }
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
        Text(text = "Pay $amount")
    }

}

@Composable
fun PayViaComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.horizontal_line),
            modifier = Modifier.weight(1f),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "or pay via")
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.horizontal_line),
            modifier = Modifier.weight(1f),
            contentDescription = null
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
    startDestination: String = Debit_CreditCard.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Debit_CreditCard.route) {
            CardHomeScreen(
                onNavigateToPinScreen = { navController.navigateSingleTopTo("pinscreen") },
            )
        }
        composable(route = Ussd.route) {
            CardHomeScreen(
                onNavigateToPinScreen = { navController.navigateSingleTopTo("pinscreen") },
            )
        }

        composable(route = BankAccount.route) {
            BankScreen(
                onNavigateToBankAccountNumberScreen = { navController.navigateSingleTopTo("pinscreen") },
            )
        }
        composable(route = "bank_account_number_screen") {
            BankAccountNumberScreen(
                onPaymentMethodClick = {}
            )
        }


        composable(route = Transfer.route) {
            BankScreen(
                onNavigateToBankAccountNumberScreen = { navController.navigateSingleTopTo("pinscreen") },
            )
        }

        composable(route = Cash.route) {
            BankScreen(
                onNavigateToBankAccountNumberScreen = { navController.navigateSingleTopTo("bank_account_number_screen") },
            )
        }
        composable(route = Ussd.route) {
            CardHomeScreen(
                onNavigateToPinScreen = { navController.navigateSingleTopTo("pinscreen") },
            )
        }

        composable("pinscreen") {
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

