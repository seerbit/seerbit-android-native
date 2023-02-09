package com.example.seerbitsdk.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.R
import com.example.seerbitsdk.SeerBitActivity
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import kotlinx.coroutines.launch




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onNavigateToCardHomeScreen: () -> Unit,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {

        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .fillMaxHeight()
                .bringIntoViewRequester(bringIntoViewRequester)
        ) {
            val rightGuideline = createGuidelineFromStart(0.05f)
            val leftGuideline = createGuidelineFromEnd(0.05f)
            val (seerbitIcon, seerbitDescriptionText, firstName, lastName, firstNameTextField, lastNameTextField, email, emailTextField, phoneNumber, phoneNumberTextField,
                amountToCharge, amountToChargeTextField, currencyDropDown, continueToPaymentButton) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.seerbit_logo),
                contentDescription = null,
                modifier = modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .constrainAs(seerbitIcon) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(parent.top, 60.dp)
                    }
            )

            Text(
                text = "Centric Gateway ltd Payment Page",
                modifier = modifier.constrainAs(seerbitDescriptionText) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(seerbitIcon.bottom, 16.dp)
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                ),
            )

            createHorizontalChain(
                firstNameTextField,
                lastNameTextField,
                chainStyle = ChainStyle.SpreadInside
            )
            OutlinedTextField(
                modifier =
                modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .constrainAs(
                        firstNameTextField
                    ) {
                        start.linkTo(rightGuideline, 16.dp)
                        end.linkTo(leftGuideline, 16.dp)
                        top.linkTo(seerbitDescriptionText.bottom, 32.dp)
                        width = Dimension.fillToConstraints
                    }, "First Name", KeyboardType.Text, true
            )

            OutlinedTextField(
                modifier =
                modifier
                    .padding(end = 4.dp)
                    .constrainAs(
                        lastNameTextField
                    ) {
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        top.linkTo(seerbitDescriptionText.bottom, 32.dp)
                        width = Dimension.fillToConstraints
                    }, "Last Name", KeyboardType.Text, true
            )

            OutlinedTextField(
                modifier =
                modifier
                    .fillMaxWidth()
                    .padding(end = 0.dp)
                    .constrainAs(
                        emailTextField
                    ) {
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        top.linkTo(firstNameTextField.bottom, 16.dp)
                        width = Dimension.matchParent
                    }, "Email", KeyboardType.Email, true
            )

            OutlinedTextField(
                modifier =
                modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .constrainAs(
                        phoneNumberTextField
                    ) {
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        top.linkTo(emailTextField.bottom, 16.dp)
                        width = Dimension.matchParent
                    }, "Phone Number", KeyboardType.Phone, true
            )

            createHorizontalChain(
                currencyDropDown, amountToChargeTextField,
                chainStyle = ChainStyle.SpreadInside
            )

            SelectCurrencyButton(modifier =
            modifier
                .width(120.dp)
                .padding(start = 8.dp, end = 4.dp)
                .constrainAs(currencyDropDown) {
                    start.linkTo(rightGuideline, 16.dp)
                    end.linkTo(amountToChargeTextField.start)
                    top.linkTo(phoneNumberTextField.bottom, 32.dp)
                })

            OutlinedTextField(
                modifier =
                modifier
                    .padding(end = 4.dp)
                    .constrainAs(
                        amountToChargeTextField
                    ) {
                        start.linkTo(currencyDropDown.end, 8.dp)
                        end.linkTo(parent.end, 0.dp)
                        top.linkTo(currencyDropDown.top)
                        bottom.linkTo(currencyDropDown.bottom)
                        width = Dimension.fillToConstraints
                    }, "Phone Number", KeyboardType.Phone, false
            )

            Button(
                onClick = onNavigateToCardHomeScreen,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .width(200.dp)
                    .height(56.dp)
                    .constrainAs(continueToPaymentButton) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(currencyDropDown.bottom, 32.dp)
                    }
            )
            {
                Text(text = "Continue to Payment", color = Color.White)

            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier, headerText: String,
    keyboardType: KeyboardType, showHeaderText: Boolean
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
                    fontWeight = FontWeight.Normal
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
                onValueChange = { newText -> value = newText },
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
fun OnBoardingScreenPreview() {
    SeerBitTheme {
        OnBoardingScreen(
            Modifier,
            onNavigateToCardHomeScreen = {

            }

        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun OutlinedTextfieldPreview() {
    SeerBitTheme {
        OutlinedTextField(Modifier, "First Name", KeyboardType.Text, true)
    }
}
