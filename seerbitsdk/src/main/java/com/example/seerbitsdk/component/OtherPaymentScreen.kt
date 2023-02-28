package com.example.seerbitsdk.navigationpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.seerbitsdk.*
import com.example.seerbitsdk.component.SeerBitNavButtonsColumn
import com.example.seerbitsdk.component.SeerbitPaymentDetailHeader
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SignalRed


@Composable
fun OtherPaymentScreen(
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp
                )
                .fillMaxWidth()


        ) {
            Spacer(modifier = Modifier.height(25.dp))
            SeerbitPaymentDetailHeader(
                charges = 0.45,
                amount = "60,000.00",
                currencyText = "",
                "Other Payment Channels",
                "",
                ""
            )
            Spacer(modifier = Modifier.height(8.dp))

            SeerBitNavButtonsColumn(
                allButtons = rallyTabRowScreens.filterNot { it.route == currentDestination?.route },
                onButtonSelected = { newScreen ->
                    navController.navigateSingleTopNoSavedState(newScreen.route)
                },
                currentButtonSelected = Transfer
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                Button(
                    onClick = onCancelButtonClicked,
                    colors = ButtonDefaults.buttonColors(backgroundColor = SignalRed),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp)

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
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    OtherPaymentScreen(
        onCancelButtonClicked = { /*TODO*/ },
        currentDestination = null,
        navController = rememberNavController()
    )
}
