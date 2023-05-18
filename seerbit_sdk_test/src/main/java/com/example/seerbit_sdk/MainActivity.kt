package com.example.seerbit_sdk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import com.example.seerbitsdk.interfaces.ActionListener
import com.example.seerbitsdk.models.query.QueryData

import com.example.seerbitsdk.startSeerBitSDK

class MainActivity : ComponentActivity(), ActionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeerBitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GoToPaymentGateway(actionListener=this)
                }
            }
        }
    }


    override fun onSuccess(data: QueryData?) {
        if (data!=null){
            // do something .....
            Toast.makeText(this, "Payment of ${data.payments?.amount} was successful", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClose() {
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
        Log.d("rerrt", "failure to show")

    }


}


@Composable
fun GoToPaymentGateway(context: Context = LocalContext.current, actionListener: ActionListener) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {

        var fullName by remember { mutableStateOf("SeerBit SeerBit") }
        var email by remember { mutableStateOf("seerbit@gmail.com") }
        var publicKey by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("09098987676") }
        var productDescription by remember { mutableStateOf("MEME") }
        var productId by remember { mutableStateOf("") }
        var vendorId by remember { mutableStateOf("") }
        var currency by remember { mutableStateOf("USD") }
        var country by remember { mutableStateOf("") }
        var pocketReference by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("43") }
        var tokenize by remember { mutableStateOf(false) }



        MyTextField(placeholder = "Enter Use Name") {
            fullName = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter Amount") {
            amount = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter email" ) {
            email = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter product Description") {
            productDescription = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter country") {
            country = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter currency") {
            currency = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        MyTextField(placeholder = "Enter vendorId") {
            vendorId = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        PhoneNumberField(placeholder = "Enter Phone Number") {
            phoneNumber = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        MyTextField(placeholder = "Enter productId") {
            productId = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        MyTextField(placeholder = "Enter pocketReference") {
            pocketReference = it
        }



        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                startSeerBitSDK(
                    context,
                    amount,
                    phoneNumber,
                    "SBPUBK_WWEQK6UVR1PNZEVVUOBNIQHEIEIM1HJC",
                    fullName,
                    email,
                    actionListener = actionListener,
                    productDescription = productDescription,
                    productId = productId,
                    pocketReference = pocketReference,
                    transactionPaymentReference = "",
                    vendorId = vendorId,
                    country = country,
                    currency = currency,
                    tokenize = false
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



@Composable
fun PhoneNumberField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterPhoneNumber: (String) -> Unit
) {
    Column {


        Card(modifier = modifier, elevation = 1.dp,
            border = BorderStroke(0.5.dp, Color.LightGray)
        ) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.length <= 11)
                        value = newText
                    onEnterPhoneNumber(newText)
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
                    keyboardType = KeyboardType.Phone,
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



