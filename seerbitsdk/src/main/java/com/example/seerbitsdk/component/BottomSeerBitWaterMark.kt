package com.example.seerbitsdk.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.R

@Composable
fun BottomSeerBitWaterMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .width(114.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = "lock icon"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.secured_by_seerbit),
            contentDescription = "secured by seerbit"
        )

    }
}