package com.example.seerbitsdk.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.R


@Composable
fun SeerbitPaymentDetailScreen(
    charges: Double,
    amount: String,
    currencyText: String,
    actionDescription: String,
) {
    Image(
        painter = painterResource(id = R.drawable.seerbit_logo),
        contentDescription = null,
        modifier = Modifier.size(40.dp)
    )
    Spacer(modifier = Modifier.height(25.dp))

    Text(
        text = "$currencyText$amount".capitalize(Locale.current),
        style = TextStyle(
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
        ),
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "subcharge $currencyText$charges",
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light
        )
    )

    Spacer(modifier = Modifier.height(21.dp))
    Text(
        text = actionDescription,
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            lineHeight = 10.sp
        ),
    )

    Spacer(modifier = Modifier.height(8.dp))
}
