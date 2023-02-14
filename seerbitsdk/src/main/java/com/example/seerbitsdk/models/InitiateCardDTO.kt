package com.example.seerbitsdk.models

import com.google.gson.annotations.SerializedName

data class InitiateCardDTO(

	@field:SerializedName("deviceType")
	val deviceType: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("cvv")
	val cvv: String? = null,

	@field:SerializedName("redirectUrl")
	val redirectUrl: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("mobileNumber")
	val mobileNumber: String? = null,

	@field:SerializedName("paymentReference")
	val paymentReference: String? = null,

	@field:SerializedName("fee")
	val fee: String? = null,

	@field:SerializedName("expiryMonth")
	val expiryMonth: String? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("channelType")
	val channelType: String? = null,

	@field:SerializedName("publicKey")
	val publicKey: String? = null,

	@field:SerializedName("expiryYear")
	val expiryYear: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("paymentType")
	val paymentType: String? = null,

	@field:SerializedName("sourceIP")
	val sourceIP: String? = null,

	@field:SerializedName("pin")
	val pin: Int? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("isCardInternational")
	val isCardInternational: String? = null,

	@field:SerializedName("rememberMe")
	val rememberMe: Boolean? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("cardNumber")
	val cardNumber: String? = null,

	@field:SerializedName("retry")
	val retry: Boolean? = null
)
