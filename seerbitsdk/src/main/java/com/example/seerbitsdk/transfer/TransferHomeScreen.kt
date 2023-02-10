package com.example.seerbitsdk.transfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.component.OtherPaymentButtonComponent
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.USSDCodeSurfaceView


@Composable
fun TransferHomeScreen(
    modifier: Modifier = Modifier,
    navigateToLoadingScreen: () -> Unit,
    onOtherPaymentButtonClicked: () -> Unit,
    onCancelPaymentButtonClicked: () -> Unit

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

            Image(
                painter = painterResource(id = R.drawable.seerbit_logo),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Transfer the exact amount including decimals",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp,
                    color = DeepRed
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))
            USSDCodeSurfaceView(ussdCodeText = "NGN 789,899.34")
            Spacer(modifier = modifier.height(20.dp))
            Text(
                text = "To",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Row(modifier.padding(12.dp)) {
                AccountDetailsSurfaceView()
            }

            Text(
                text = "Account number can only be used once",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Faktpro,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 10.sp,
                    color = DeepRed
                ),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )


            Spacer(modifier = modifier.height(25.dp))
            AuthorizeButton(
                buttonText = "I have completed this bank transfer",
                onClick = navigateToLoadingScreen
            )
            Spacer(modifier = Modifier.height(72.dp))

            OtherPaymentButtonComponent(
                onOtherPaymentButtonClicked = onOtherPaymentButtonClicked,
                onCancelButtonClicked = onCancelPaymentButtonClicked
            )
        }


    }

}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun TransferHomeScreenPreview() {
    SeerBitTheme {
        TransferHomeScreen(
            navigateToLoadingScreen = { /*TODO*/ },
            onOtherPaymentButtonClicked = { /*TODO*/ }) {

        }
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
                icon = R.drawable.ic_copy
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
            CustomAccountDetailsRow(
                leftHandText = "Validity",
                rightHandText = "30 Minutes",
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = rightHandText)
            Spacer(modifier = Modifier.width(4.dp))
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null
                )
            }
        }
    }

}
