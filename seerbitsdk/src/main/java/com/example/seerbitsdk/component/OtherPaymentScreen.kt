package com.example.seerbitsdk.component

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
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SignalRed


@Composable
fun OtherPaymentScreen(
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit,
    currentDestination: NavDestination?,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState?
) {

    merchantDetailsState?.data?.let { merchantDetailsData ->
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
                    charges = merchantDetailsData.payload?.vatFee?.toDouble()!!,
                    amount = "20.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "Other Payment Channels",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )
                Spacer(modifier = Modifier.height(8.dp))

                val addedButtons: ArrayList<SeerBitDestination> = arrayListOf()
                merchantDetailsState.data.payload?.country?.defaultPaymentOptions?.forEach {

                    if (it?.code == "CARD" && it.status == "ACTIVE") {
                        addedButtons.add(Debit_CreditCard)
                    }
                    if (it?.code == "TRANSFER" && it.status == "ACTIVE") {
                        addedButtons.add(Transfer)
                    }
                    if (it?.code == "USSD" && it.status == "ACTIVE") {
                        addedButtons.add(UssdSelectBank)
                    }
                    if (it?.code == "ACCOUNT" && it.status == "ACTIVE") {
                        addedButtons.add(BankAccount)
                    }
                    if (it?.code == "MOMO" && it.status == "ACTIVE") {
                        addedButtons.add(MOMO)
                    }
                    if (it?.code == "POS" && it.status == "ACTIVE") {
                        //addedButtons.add(MOMO)
                    }
                }

                SeerBitNavButtonsColumn(
                    allButtons = addedButtons,
                    onButtonSelected = { newScreen ->

                        navController.navigateSingleTopNoSavedState(newScreen.route)

                    },
                    currentButtonSelected = Transfer
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

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
}

fun generateTransferDTO (merchantDetailsState: MerchantDetailsState?){

}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    OtherPaymentScreen(
        onCancelButtonClicked = { /*TODO*/ },
        currentDestination = null,
        navController = rememberNavController(),
        merchantDetailsState = null
    )
}
