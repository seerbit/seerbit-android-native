package com.example.seerbitsdk.models

import com.google.gson.annotations.SerializedName

data class CardOTPDTO(

	@field:SerializedName("transaction")
	val transaction: Transaction? = null
): OtpDTO

data class Transaction(

	@field:SerializedName("linkingreference")
	val linkingreference: String? = null,

	@field:SerializedName("otp")
	val otp: String? = null
)
