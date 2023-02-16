package com.example.seerbitsdk.component

fun String.isValidCardNumber() : Boolean{
    return this.length == 16
}

fun String.isValidCvv() : Boolean{
    return this.length == 3
}
fun String.isValidCardExpiryMonth() : Boolean{
    return if(this == "")
        false
    else
        this.length ==2 && this.toInt()<= 12
}
fun String.isValidCardExpiryYear() : Boolean{
    return if(this == "")
        false
    else
        this.length ==4
}

fun generateRandomReference() : String {
    var str = "ABCDEFGHIJKLMNOPQRSTNVabcdefghijklmnopqrstuvwxyzABCD123456789"
    var password = ""
    for (i in 1..100) {
        password += str.random()
    }
    return password
}