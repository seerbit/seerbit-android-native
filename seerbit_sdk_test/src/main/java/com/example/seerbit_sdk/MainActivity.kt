package com.example.seerbit_sdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbit_sdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.R
import com.example.seerbitsdk.bank.BankAccountNumberField
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
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {

        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var publicKey by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }

        MyTextField(placeholder = "Enter Use Name") {
            fullName = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter Amount") {
            amount = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter User email") {
            email = it
        }
        Spacer(modifier = Modifier.height(10.dp))


        BankAccountNumberField(placeholder = "Enter Phone Number") {
            phoneNumber = it
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {

                startSeerBitSDK(
                    context,
                    amount,
                    phoneNumber,
                    "SBPUBK_XKFXPR86RXN0RRWXNI5MK15KJMKEYHUD",
                    fullName,
                    email
                )

            }, modifier = Modifier
                .width(100.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "pay amount")
        }
    }

}


@Composable
fun MyTextField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBVN: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 1.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->

                        value = newText
                    onEnterBVN(newText)
                },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}


