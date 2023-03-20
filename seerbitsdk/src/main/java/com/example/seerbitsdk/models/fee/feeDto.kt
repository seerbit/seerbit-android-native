package com.example.seerbitsdk.models.fee

import com.google.gson.annotations.SerializedName

data class FeeDto(
    val amount: String,
    val type: String,
    val key: String
)
