package com.example.seerbitsdk.models.otp

import com.example.seerbitsdk.models.OtpDTO

import com.google.gson.annotations.SerializedName

data class MomoOtpDto(

    @field:SerializedName("transaction")
    val transaction: Transaction? = null
): OtpDTO

data class Transaction(

    @field:SerializedName("linkingreference")
    val linkingreference: String? = null,

    @field:SerializedName("otp")
    val otp: String? = null
)
