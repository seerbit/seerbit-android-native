package com.example.seerbitsdk.component

import android.content.Context
import android.widget.Toast
import androidx.compose.material.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.text.DecimalFormat
import java.util.*

fun String.isValidCardNumber(): Boolean {
    return this.length >= 16
}

fun String.isValidCvv(): Boolean {
    return this.length == 3
}

fun String.isValidCardExpiryMonth(): Boolean {
    if (this == "")
        return false
    return if (this.length >= 2)
        this.substring(0, 2).toInt() <= 12
    else false
}

fun String.isValidCardExpiryYear(): Boolean {
    return if (this == "")
        false
    else
        this.length == 4
}

fun validateCardDetails(
    isValidCvv: Boolean,
    isValidCardNumber: Boolean,
    isValidCardExpiryMonth: Boolean
): Boolean {
    return isValidCvv && isValidCardNumber && isValidCardExpiryMonth
}

fun String.maskedPhoneNumber(): String {
    return if (this.length >= 7) {
        "*******${this.substring(8)}"
    } else
        "*****${this.last()}"
}

fun formatAmount(amount: Double?): String {
    return AmountDecimalFormat.format(amount)
}


fun generateRandomReferenceTwo(): String {

    val str = "ABCDEFGHIJKLMNOPQRSTNVabcdef6ghijklmnopqrstuvwxyzABCD123456789"
    var password = ""
    for (i in 1..8) {
        password += str.random()
    }
    return "SBT-T" + UUID.randomUUID().toString().substring(0..15)
}


private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")


@Composable
fun showSnackBar(context : Context = LocalContext.current, toastMessage : String){
   Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}