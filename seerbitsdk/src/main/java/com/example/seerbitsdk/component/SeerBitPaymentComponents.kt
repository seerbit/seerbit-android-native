package com.example.seerbitsdk.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.R
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun SeerbitPaymentDetailHeader(
    charges: Double,
    amount: String,
    currencyText: String,
    actionDescription: String,
    businessName: String,
    email: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.seerbit_logo),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.height(50.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = businessName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = email, style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "$currencyText${formatAmount(amount.toDouble())}".capitalize(Locale.current),
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Fees $currencyText$charges",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Light
            )
        )

        Spacer(modifier = Modifier.height(21.dp))
        Text(
            text = actionDescription,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SeerbitPaymentDetailHeaderTwo(
    charges: Double,
    amount: String,
    currencyText: String,
    businessName: String,
    email: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.seerbit_logo),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.height(50.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = businessName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = email, style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "$currencyText${formatAmount(amount.toDouble())}".capitalize(Locale.current),
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Fees $currencyText$charges",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Light
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SeerbitPaymentDetailHeaderNoFee(
    charges: String = "",
    amount: String,
    currencyText: String,
    actionDescription: String,
    businessName: String,
    email: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.seerbit_logo),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.height(50.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = businessName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = email, style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Faktpro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "$currencyText${formatAmount(amount.toDouble())}".capitalize(Locale.current),
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Light
            )
        )

        Spacer(modifier = Modifier.height(21.dp))
        Text(
            text = actionDescription,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Preview(showBackground = true, widthDp = 320)
@Composable
fun SeerbitPaymentDetailHeaderPreview() {
    SeerBitTheme {
        SeerbitPaymentDetailHeader(
            charges = 1.5,
            amount = "60,000",
            currencyText = "NGN",
            actionDescription = "",
            "Centric GateWay Ltd",
            "adeifetaiwo50@gmail.com"

        )
    }
}

