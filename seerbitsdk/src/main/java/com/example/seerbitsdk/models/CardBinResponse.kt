package com.example.seerbitsdk.models

import com.google.gson.annotations.SerializedName

data class CardBinResponse(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("cardBin")
	val cardBin: String? = null,

	@field:SerializedName("cardName")
	val cardName: String? = null,

	@field:SerializedName("transactionReference")
	val transactionReference: String? = null,

	@field:SerializedName("nigeriancard")
	val nigeriancard: Boolean? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: String? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null
)
