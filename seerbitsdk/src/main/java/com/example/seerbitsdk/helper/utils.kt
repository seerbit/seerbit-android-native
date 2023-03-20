package com.example.seerbitsdk.helper

import com.example.seerbitsdk.models.home.MerchantDetailsResponse

enum class TransactionType(val type: String) {
    CARD("CARD"),
    TRANSFER("TRANSFER"),
    MOMO("MOMO"),
    USSD("USSD"),
    ACCOUNT("ACCOUNT")
}

fun displayPaymentMethod(paymentMethod: String, merchantDetailsResponse: MerchantDetailsResponse?): Boolean {
//    if (paymentMethod == TransactionType.USSD.type) {
    if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {
        //merchant has payment configuration
        merchantDetailsResponse.payload.paymentConfigs.forEach {
            if (it?.code == paymentMethod) {
                if (merchantDetailsResponse.payload.channelOptionStatus?.isNotEmpty() == true) {
                    merchantDetailsResponse.payload.channelOptionStatus.forEach { channelOptionStatus ->
                        if (it.code == channelOptionStatus?.code && channelOptionStatus.allowOption == true) return true
                    }
                } else {
                    //channel option status is empty
                    merchantDetailsResponse.payload.paymentConfigs.forEach { paymentConfigs ->
                        if (paymentConfigs?.code == paymentMethod && paymentConfigs.allowOption == true) return true
                    }
                }
            }
        }
    } else {
        merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
            if (it?.code == paymentMethod) {

                if (merchantDetailsResponse.payload.channelOptionStatus?.isNotEmpty() == true) {
                    merchantDetailsResponse.payload.channelOptionStatus.forEach { channelOptionStatus ->
                        if (it.code == channelOptionStatus?.code && channelOptionStatus.allowOption == true) return true
                    }
                } else {
                    merchantDetailsResponse.payload.country.defaultPaymentOptions.forEach { defaultPaymentOptions ->
                        if (defaultPaymentOptions?.code == paymentMethod && defaultPaymentOptions.allowOption == true) return true
                    }
                }
            }
        }
    }
//    }

    return false
}
