package com.example.seerbitsdk.bank

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.seerbitsdk.ErrorDialog
import com.example.seerbitsdk.R
import com.example.seerbitsdk.card.AuthorizeButton
import com.example.seerbitsdk.card.showCircularProgress
import com.example.seerbitsdk.component.Dummy
import com.example.seerbitsdk.component.Route
import com.example.seerbitsdk.component.SeerbitPaymentDetailHeader
import com.example.seerbitsdk.component.YES
import com.example.seerbitsdk.models.RequiredFields
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.navigateSingleTopNoSavedState
import com.example.seerbitsdk.navigateSingleTopTo
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.viewmodels.TransactionViewModel
import com.google.gson.Gson

@Composable
fun BankAccountDOBScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    merchantDetailsState: MerchantDetailsState,
    transactionViewModel: TransactionViewModel,
    bankAccountNumber: String,
    bvn: String,
    bankCode: String,
    requiredFields: RequiredFields?,
    bankName : String?,

) {

    var dob by remember { mutableStateOf("") }
    var json by remember { mutableStateOf(Uri.encode(Gson().toJson(requiredFields))) }
    var amount : String = "60,000"
    // if there is an error loading the report
    if (merchantDetailsState?.hasError!!) {
        ErrorDialog(message = merchantDetailsState.errorMessage ?: "Something went wrong")
    }

    if (merchantDetailsState.isLoading) {
        showCircularProgress(showProgress = true)
    }

    merchantDetailsState.data?.let { merchantDetailsData ->
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

                SeerbitPaymentDetailHeader(
                    charges =  merchantDetailsData.payload?.vatFee?.toDouble()!!,
                    amount = "20.00",
                    currencyText = merchantDetailsData.payload.defaultCurrency!!,
                    "Please Enter your birthday",
                    merchantDetailsData.payload.businessName!!,
                    merchantDetailsData.payload.supportEmail!!
                )

                Spacer(modifier = modifier.height(10.dp))
                //Show Birthday
                BirthDayInputField(Modifier, "DD / MM / YYYY ") {
                    dob = it
                }

                Spacer(modifier = modifier.height(30.dp))

                AuthorizeButton(
                    buttonText = "Pay NGN$amount",
                    onClick = {

                        if (dob.isNotEmpty()) {

                            navController.navigateSingleTopNoSavedState(
                                "${Route.BANK_ACCOUNT_OTP_SCREEN}/$bankName/$json/$bankCode/$bankAccountNumber/$bvn/$dob"
                            )

                        }
                    }, true
                )


            }


        }
    }

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun BankAccountDOBScreenPreview() {
    val viewModel: TransactionViewModel by viewModel()
    SeerBitTheme {

    }
}


@Composable
fun BirthDayInputField(
    modifier: Modifier = Modifier, placeholder: String,
    onEnterBirthday: (String) -> Unit
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
                    if (newText.length <= 8)
                        value = newText
                    onEnterBirthday(newText)
                },
                visualTransformation = { birthdayInputFormat(it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = Color.Transparent,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray

                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
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


fun birthdayInputFormat(text: AnnotatedString): TransformedText {

    // change the length
    val annotatedString = AnnotatedString.Builder().run {
        for (i in text.indices) {
            append(text[i])
            if (i == 1 || i == 3) {
                append("/")
            }
        }
        toAnnotatedString()
    }


    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset + 1
            if (offset <= 7) return offset + 2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset - 1
            if (offset <= 7) return offset -2
            return 8
        }
    }
    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}


