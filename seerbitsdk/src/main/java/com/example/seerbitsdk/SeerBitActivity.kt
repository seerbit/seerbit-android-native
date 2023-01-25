package com.example.seerbitsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme

class SeerBitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeerBitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SeerBitTheme {
        Greeting("Android")
    }
}

@Composable
fun bottomSeerBitWaterMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .width(114.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = "seerbit watermark"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.secured_by_seerbit),
            contentDescription = "seerbit watermark"
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SeerBitWaterMarkPreview() {
    SeerBitTheme {
        bottomSeerBitWaterMark()
    }
}

@Composable
fun HeaderScreen(
    charges: Double,
    amount: String,
    currencyText: String,
    actionDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {


        Column(
            modifier = modifier
                .padding(
                    start = 21.dp,
                    end = 21.dp
                )
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(25.dp))
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
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            CardDetailsScreen()
            Spacer(modifier = modifier.height(13.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var value by remember {
                    mutableStateOf(false)
                }
                Checkbox(
                    checked = value,
                    onCheckedChange = { newValue -> value = newValue },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = LighterGray,
                        checkedColor = Color.LightGray
                    )
                )
                Text(text = "Remember my information on this device")
            }

            Spacer(modifier = Modifier.height(8.dp))
            PayButton(amount = "NGN 60,000")
            Spacer(modifier = Modifier.height(16.dp))
            PayViaComponent()
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.padding(0.dp)) {
                items(paymentTypeData.paymentData) { item ->
                    PaymentOptionButtons(
                        paymentName = item.name,
                        paymentDescription = item.Desc
                    )
                }
            }
        }


        //** this is the bottom watter mark
        Row(
            modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            bottomSeerBitWaterMark()
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun HeaderScreenPreview() {
    SeerBitTheme {
        HeaderScreen(
            charges = 0.45,
            amount = "60,000.00",
            currencyText = "NGN",
            "Debit/credit Card Details"
        )
    }
}

@Composable
fun CardDetailsScreen(modifier: Modifier = Modifier) {

    Column {


        Card(modifier = modifier, elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(8.dp),
                placeholder = {
                    Text(
                        text = "Card Number",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        Spacer(modifier = modifier.height(16.dp))
        MMM_CVVScreen(modifier = modifier)

    }
}

@Composable
fun MMM_CVVScreen(modifier: Modifier) {
    Row(modifier = Modifier) {

        Card(modifier = modifier.weight(1f), elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                placeholder = {
                    Text(
                        text = "MM/YY",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.vertical_divider_line),
            contentDescription = "dividing line"
        )
        Card(modifier = modifier.weight(1f), elevation = 4.dp) {
            var value by remember { mutableStateOf("") }
            Image(
                painter = painterResource(id = R.drawable.filled_bg_white),
                contentDescription = null
            )
            OutlinedTextField(
                value = value,
                onValueChange = { newText -> value = newText },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cvv_card),
                        contentDescription = null
                    )
                },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                placeholder = {
                    Text(
                        text = "CVV",
                        style = TextStyle(fontSize = 14.sp),
                        color = Color.LightGray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

    }
}

@Preview(showBackground = true, widthDp = 518)
@Composable
fun MMM_CVVScreenPreview() {
    SeerBitTheme {
        MMM_CVVScreen(modifier = Modifier)
    }
}

@Preview(showBackground = true, widthDp = 518)
@Composable
fun CardDetailsPreview() {
    SeerBitTheme {
        CardDetailsScreen()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentOptionButtons(
    paymentName: String,
    paymentDescription: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {/*TODO*/},
        shape = RoundedCornerShape(8.dp),

        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(4.dp),
        color = LighterGray,

    ) {

        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = paymentName)
            Text(text = paymentDescription)
        }
    }

}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun paymentOptionsButtonPreview() {
    SeerBitTheme {
        LazyColumn(modifier = Modifier.padding(0.dp)) {
            items(paymentTypeData.paymentData) { item ->
                PaymentOptionButtons(
                    paymentName = item.name,
                    paymentDescription = item.Desc
                )
            }
        }
    }
}

@Composable
fun PayButton(amount: String) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()

    ) {
        Text(text = "Pay $amount")
    }

}

@Composable
fun PayViaComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.horizontal_line), modifier = Modifier.weight(1f), contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "or pay via")
        Spacer(modifier = Modifier.width(4.dp))
        Image(painter = painterResource(id = R.drawable.horizontal_line),  modifier = Modifier.weight(1f), contentDescription = null)

    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun payViaComponentPreview() {
    SeerBitTheme {
        PayViaComponent()
    }
}
