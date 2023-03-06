package com.example.seerbitsdk.component

import java.text.DecimalFormat

fun String.isValidCardNumber(): Boolean {
    return this.length == 16
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

fun generateRandomReference(): String {
    var str = "ABCDEFGHIJKLMNOPQRSTNVabcdefghijklmnopqrstuvwxyzABCD123456789"
    var password = ""
    for (i in 1..100) {
        password += str.random()
    }
    return password
}

fun String.maskedPhoneNumber(): String {
    return if (this.length >= 5) {
        "*****${this.substring(5)}"
    } else
        "*****${this.last()}"
}

fun formatAmount(amount: Double?): String {
    return AmountDecimalFormat.format(amount)
}

private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")