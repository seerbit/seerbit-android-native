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
    val cardToken: String? = null
)

data class Data(

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("payments")
    val payments: Payments? = null,

    @SerializedName("message")
    val message: String? = null
)
