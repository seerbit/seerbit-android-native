package com.example.seerbitsdk.models.momo

import com.google.gson.annotations.SerializedName

data class MomoNetworkResponse(

	val momoNetworkResponse: List<MomoNetworkResponseItem?>? = null
)

data class MomoNetworkResponseItem(

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("networkCode")
	val networkCode: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("networks")
	val networks: String? = null,

	@field:SerializedName("processorCode")
	val processorCode: String? = null,

	@field:SerializedName("voucherCode")
	val voucherCode: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null
)
