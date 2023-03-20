package com.example.seerbit_sdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seerbit_sdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.startSeerBitSDK

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeerBitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    goToPaymentGateway()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SeerBitTheme {
        Greeting("Android")
    }
}


@Preview(showBackground = true, heightDp = 300)
@Composable
fun goToPaymentGateway(context: Context = LocalContext.current) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {

            startSeerBitSDK(
                context,
                "30",
                "08035764450",
                "SBPUBK_TCDUH6MNIDLHMJXJEJLBO6ZU2RNUUPHI",
                "Bamigbaye Bukola",
                "bukkyize@gmail.com"
            )

        }, modifier = Modifier.width(100.dp).align(Alignment.CenterHorizontally)) {
            Text(text = "pay amount")
        }
    }

}

