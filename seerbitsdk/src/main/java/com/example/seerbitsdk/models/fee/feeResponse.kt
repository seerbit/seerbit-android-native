package com.example.seerbitsdk.models.fee

import com.google.gson.annotations.SerializedName

data class FeeResponse(
    val feeEncrypted: String,
    val fee: String
)
