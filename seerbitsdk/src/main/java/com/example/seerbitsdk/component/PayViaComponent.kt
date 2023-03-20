package com.example.seerbitsdk.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.R


@Composable
fun PayViaComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.horizontal_line),
            modifier = Modifier.weight(1f),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "or pay via")
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.horizontal_line),
            modifier = Modifier.weight(1f),
            contentDescription = null
        )

    }
}