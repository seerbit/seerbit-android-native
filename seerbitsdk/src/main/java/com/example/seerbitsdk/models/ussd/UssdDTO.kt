package com.example.seerbitsdk.models.ussd

import com.example.seerbitsdk.models.TransactionDTO
import com.google.gson.annotations.SerializedName

data class UssdDTO(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("bankCode")
	val bankCode: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("redirectUrl")
	val redirectUrl: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("mobileNumber")
	val mobileNumber: String? = null,

	@field:SerializedName("paymentReference")
	var paymentReference: String? = null,

	@field:SerializedName("fee")
	val fee: String? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("channelType")
	val channelType: String? = null,

	@field:SerializedName("publicKey")
	val publicKey: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("paymentType")
	val paymentType: String? = null,

	@field:SerializedName("ddeviceType")
	val ddeviceType: String? = null,

	@field:SerializedName("sourceIP")
	val sourceIP: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("productDescription")
	val productDescription: String? = null,

	@field:SerializedName("retry")
	val retry: Boolean? = null,

	@field:SerializedName("pocketReference")
	val pocketReference: String?,

	@field:SerializedName("vendorId")
	val vendorId: String?
)
