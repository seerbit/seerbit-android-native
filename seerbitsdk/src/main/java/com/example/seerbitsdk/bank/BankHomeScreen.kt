package com.example.seerbitsdk.bank

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.component.PayViaComponent
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.ui.theme.SeerBitTheme

@Composable
fun BankScreen(
    modifier: Modifier = Modifier,
    onNavigateToBankAccountNumberScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    var showPinScreen by remember { mutableStateOf(false) }
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

            SeerbitPaymentDetailScreen(
                charges = 0.45,
                amount = "60,000.00",
                currencyText = "NGN",
                "Choose your bank to start this payment",
                "",
                ""
            )

            Spacer(modifier = Modifier.height(21.dp))

            Spacer(modifier = Modifier.height(20.dp))
            SelectBankButton()
            Spacer(modifier = modifier.height(20.dp))

            Spacer(modifier = modifier.height(10.dp))
            AuthorizeButton(
                buttonText = "Pay $50",
                onClick = onNavigateToBankAccountNumberScreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(60.dp))

            OtherPaymentButtonComponent(
                onOtherPaymentButtonClicked = { /*TODO*/ },
                onCancelButtonClicked = {})

        }
    }
}


@Composable
fun SelectBankButton(modifier: Modifier = Modifier) {

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
        Card(modifier = modifier, elevation = 4.dp) {

            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },

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
        "Guaramtee Trust Bank",
        "Uba",
        "Union Bank",
        "First Bank",
        "Hyderabad",
        "Bengaluru",
        "Pune",
        "Delhi",
        "Mumbai",
        "Chennai",
        "Kolkata",
        "Hyderabad",
        "Bengaluru",
        "Pune"
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
fun SelectBankButtonPreview() {
    SeerBitTheme {
        BankScreen(
            onNavigateToBankAccountNumberScreen = {},
            currentDestination = null,
            navController = rememberNavController()
        )
    }
}

