package com.example.seerbitsdk.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.PayViaComponent
import com.example.seerbitsdk.Transfer
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.component.SeerBitNavButtonsColumn
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.navigateSingleTopTo
import com.example.seerbitsdk.rallyTabRowScreens
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.USSDCodeSurfaceView


@Composable
fun TransferHomeScreen(
    modifier: Modifier = Modifier,
    navigateToLoadingScreen: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController

) {
    var showLoadingScreen by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {


        Column(
            modifier = modifier
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(21.dp))
            SeerbitPaymentDetailScreen(
                charges = 0.0,
                amount = "",
                currencyText = "",
                ""
            )

            Text(
                text = "Transfer",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))
            USSDCodeSurfaceView(ussdCodeText = "NGN 789,899.34")
            Spacer(modifier = modifier.height(20.dp))
            Text(
                text = "To the account details below",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Row(modifier.padding(12.dp)) {
                AccountDetailsSurfaceView()
            }


            Spacer(modifier = modifier.height(20.dp))
            AuthorizeButton(
                buttonText = "Confirm Payment",
                onClick = { showLoadingScreen = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }


        PayViaComponent()
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        SeerBitNavButtonsColumn(
            allButtons = rallyTabRowScreens.filterNot { it.route == currentDestination?.route },
            onButtonSelected = { newScreen ->
                navController.navigateSingleTopTo(newScreen.route)
            },
            currentButtonSelected = Transfer
        )

    }

}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun TransferHomeScreenPreview() {
    SeerBitTheme {
        TransferHomeScreen(
            navigateToLoadingScreen = {},
            currentDestination = null,
            navController = rememberNavController()
        )
    }
}

@Composable
fun AccountDetailsSurfaceView() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                top = 16.dp,
                end = 0.dp,
                start = 0.dp,
                bottom = 16.dp
            ),
        shape = RoundedCornerShape(8.dp),
        color = LighterGray
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            CustomAccountDetailsRow(
                leftHandText = "Account Number",
                rightHandText = "0228290130",
                icon = null
            )
            CustomAccountDetailsRow(
                leftHandText = "Bank",
                rightHandText = "Guarantee Trust Bank",
                icon = null
            )
            CustomAccountDetailsRow(
                leftHandText = "Beneficiary Name",
                rightHandText = "Seerbit(Empire)",
                icon = null
            )

        }
    }
}

@Composable
fun CustomAccountDetailsRow(leftHandText: String, rightHandText: String, icon: Int?) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = leftHandText)
        Row() {

        }
        Text(text = rightHandText)
    }

}
