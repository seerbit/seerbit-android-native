package com.example.seerbitsdk.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.ui.theme.DeepRed
import com.example.seerbitsdk.ui.theme.Faktpro
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SignalRed

@Composable
fun OtherPaymentButtonComponent(
    modifier: Modifier = Modifier,
    onOtherPaymentButtonClicked : () -> Unit,
    onCancelButtonClicked : () -> Unit
){
Row(modifier = Modifier.fillMaxWidth()) {
    Button(
        onClick = onOtherPaymentButtonClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = LighterGray),
        shape = RoundedCornerShape(4.dp),

        modifier = Modifier
            .height(50.dp)
            .weight(1f)
            .padding(end = 8.dp)

    ) {
        Text(
            text = "Change Payment Method",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp
            )
        )
    }

    Button(
        onClick = onCancelButtonClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = SignalRed),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .height(50.dp)
            .weight(1f)

    ) {
        Text(
            text = "Cancel Payment",

            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Faktpro,
                fontWeight = FontWeight.Normal,
                lineHeight = 10.sp,
                color = DeepRed,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }

}
}