package com.example.seerbitsdk.ussd

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
import com.example.seerbitsdk.component.PayViaComponent
import com.example.seerbitsdk.Transfer
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.component.CustomLinearProgressBar
import com.example.seerbitsdk.component.SeerBitNavButtonsColumn
import com.example.seerbitsdk.component.SeerbitPaymentDetailScreen
import com.example.seerbitsdk.navigateSingleTopTo
import com.example.seerbitsdk.rallyTabRowScreens
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun USSDHomeScreen(
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
                charges = 0.45,
                amount = "60,000.00",
                currencyText = "NGN",
                "",
                "",
                ""
            )

            // if loadingScreen value is false
            if (!showLoadingScreen) {
                Text(
                    text = "Dial the code below to complete this payment.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 10.sp
                    ),
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))
                USSDCodeSurfaceView(ussdCodeText = "*737*000*99099#")
                Spacer(modifier = modifier.height(20.dp))
                AuthorizeButton(
                    buttonText = "Confirm Payment",
                    onClick = { showLoadingScreen = true }
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                showLoaderLayout()
            }

            PayViaComponent()
            Spacer(modifier = Modifier.height(8.dp))

        }
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


@Composable
fun USSDCodeSurfaceView(ussdCodeText: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        color = LighterGray
    ) {
        Row(horizontalArrangement = Arrangement.Center) {

            Text(
                text = ussdCodeText, style = TextStyle(
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp
                ),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun USSDCodeSurfaceViewPreview() {
    SeerBitTheme {
        USSDCodeSurfaceView(ussdCodeText = "*737*000*99099#")
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    SeerBitTheme {
        USSDHomeScreen(
            navigateToLoadingScreen = { /*TODO*/ },
            currentDestination = null,
            navController = rememberNavController()
        )
    }
}

@Composable
fun showLoaderLayout() {
    Text(
        text = "Hold on tight while we confirm your payment.",
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            lineHeight = 10.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    CustomLinearProgressBar(showProgress = true)
    Spacer(modifier = Modifier.height(25.dp))


}