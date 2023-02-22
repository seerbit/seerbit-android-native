package com.example.seerbitsdk.models

import com.google.gson.annotations.SerializedName

data class GetBanksResponse(

	@field:SerializedName("data")
	val availableBankData: AvailableBankData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class MerchantBanksItem(

	@field:SerializedName("bankCode")
	val bankCode: String? = null,

	@field:SerializedName("requiredFields")
	val requiredFields: RequiredFields? = null,

	@field:SerializedName("minimumAmount")
	val minimumAmount: Double? = null,

	@field:SerializedName("bankName")
	val bankName: String? = null,

	@field:SerializedName("operation")
	val operation: String? = null,

	@field:SerializedName("directConnection")
	val directConnection: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class AvailableBankData(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("merchantBanks")
	val merchantBanks: List<MerchantBanksItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class RequiredFields(

	@field:SerializedName("isBankCode")
	val isBankCode: String? = null,

	@field:SerializedName("accountName")
	val accountName: String? = null,

	@field:SerializedName("mobileNumber")
	val mobileNumber: String? = null,

	@field:SerializedName("dateOfBirth")
	val dateOfBirth: String? = null,

	@field:SerializedName("bvn")
	val bvn: String? = null,

	@field:SerializedName("accountNumber")
	val accountNumber: String? = null
)
