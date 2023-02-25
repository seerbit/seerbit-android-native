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
import androidx.compose.ui.unit.*
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.Route
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.models.MerchantBanksItem
import com.example.seerbitsdk.models.ussd.UssdBankData
import com.example.seerbitsdk.navigateSingleTopNoPopUpToHome
import com.example.seerbitsdk.navigateSingleTopTo
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.SelectBankViewModel

@Composable
fun BankScreen(
    modifier: Modifier = Modifier,
    navigateToUssdHomeScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    onConfirmPaymentClicked: () -> Unit,
    merchantDetailsState: MerchantDetailsState?,
    selectBankViewModel: SelectBankViewModel
) {
    var bankCode by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showCircularProgressBar by remember { mutableStateOf(false) }

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
                        start = 21.dp,
                        end = 21.dp
                    )
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                SeerbitPaymentDetailScreen(

                    charges = merchantDetailsData.payload?.cardFee?.visa!!.toDouble(),
                    amount = "60,000.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "Choose your bank to start this payment",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )
                Spacer(modifier = Modifier.height(41.dp))

                if (showErrorDialog) {
                    ErrorDialog(message = "Kindly Select a bank")
                }
                if(showCircularProgressBar){
                    showCircularProgress(showProgress = true)
                }


                val availableBanksState = selectBankViewModel.availableBanksState.value

                if(availableBanksState.hasError){
                    showCircularProgressBar = false
                    showErrorDialog = true
                }

                if(availableBanksState.isLoading){
                    showCircularProgressBar = true
                }

                var merchantBankList : List<MerchantBanksItem?>? = listOf()
                availableBanksState.data?.availableBankData?.let {
                    showCircularProgressBar = false
                    merchantBankList = it.merchantBanks!!

                }


                BankSelectBankButton (merchantBankList = merchantBankList){
                    bankCode = it
                }

                Spacer(modifier = modifier.height(40.dp))

                AuthorizeButton(
                    buttonText = "Pay NGN60,000",
                    onClick = {
                        if(bankCode.isNotEmpty()) {
                            navController.navigateSingleTopTo(
                                "${Route.BANK_ACCOUNT_NUMBER_SCREEN}/eaadd/sdfjk/skfkj/sjfoj"
                            )
                        }
                        else {
                            showErrorDialog = true
                        }
                    }, !showCircularProgressBar
                )

            }
        }
    }
}


@Composable
fun BankSelectBankButton(modifier: Modifier = Modifier, merchantBankList : List<MerchantBanksItem?>?, onBankCodeSelected: (String) -> Unit) {

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
        DropdownMenu(

            expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.width(
                with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            merchantBankList?.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label?.bankName!!
                    onBankCodeSelected(label.bankCode!!)
                    expanded = false
                }) {
                    Text(text = label?.bankName!!)
                }
            }

        }

    }


}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDSelectBankButtonPreview() {
    SeerBitTheme {
        val selectBankViewModel : SelectBankViewModel = SelectBankViewModel()
       BankScreen(
           navigateToUssdHomeScreen = { /*TODO*/ },
           currentDestination = null,
           navController = rememberNavController(),
           onConfirmPaymentClicked = { /*TODO*/ },
           merchantDetailsState = null,
           selectBankViewModel = selectBankViewModel
       )
    }
}

