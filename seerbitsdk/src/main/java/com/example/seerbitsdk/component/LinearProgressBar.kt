package com.example.seerbitsdk.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.ussd.USSDHomeScreen


@Composable
fun CustomLinearProgressBar(showProgress : Boolean){
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
                backgroundColor = LighterGray,
            color = Color.DarkGray //progress color
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun ProgressBarPreview() {
    SeerBitTheme {
        CustomLinearProgressBar(showProgress = true)
    }
}
