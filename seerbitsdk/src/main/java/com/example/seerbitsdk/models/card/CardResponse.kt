package com.example.seerbitsdk.models.card

import com.google.gson.annotations.SerializedName

data class CardResponse(

    @SerializedName("data")
    val data: Data? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null
)

data class Payments(

    @SerializedName("redirectUrl")
    val redirectUrl: String? = null,

    @SerializedName("paymentReference")
    val paymentReference: String? = null,

    @SerializedName("linkingReference")
    val linkingReference: String? = null,

    @SerializedName("cardToken")
    val cardToken: String? = null,

    @SerializedName("ussdDailCode")
    val ussdDailCode: String? = null,

    @SerializedName("walletName")
    val walletName: String? = null,

    @SerializedName("wallet")
    val wallet: String? = null,

    @SerializedName("accountNumber")
    val accountNumber: String? = null,

    @SerializedName("bankName")
    val bankName: String? = null,




)

data class Data(

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("payments")
    val payments: Payments? = null,

    @SerializedName("message")
    val message: String? = null
)
