package com.example.seerbitsdk.models.query

import com.google.gson.annotations.SerializedName

data class QueryTransactionResponse(

	@field:SerializedName("data")
	val data: QueryData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Payments(

	@field:SerializedName("deviceType")
	val deviceType: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("reason")
	val reason: String? = null,

	@field:SerializedName("amount")
	val amount: Double? = null,

	@field:SerializedName("redirecturl")
	val redirecturl: String? = null,

	@field:SerializedName("mobilenumber")
	val mobilenumber: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("maskedPan")
	val maskedPan: String? = null,

	@field:SerializedName("fee")
	val fee: String? = null,

	@field:SerializedName("paymentReference")
	val paymentReference: String? = null,

	@field:SerializedName("businessName")
	val businessName: String? = null,

	@field:SerializedName("channelType")
	val channelType: String? = null,

	@field:SerializedName("publicKey")
	val publicKey: String? = null,

	@field:SerializedName("paymentType")
	val paymentType: String? = null,

	@field:SerializedName("mode")
	val mode: String? = null,

	@field:SerializedName("transactionProcessedTime")
	val transactionProcessedTime: String? = null,

	@field:SerializedName("gatewayCode")
	val gatewayCode: String? = null,

	@field:SerializedName("sourceIP")
	val sourceIP: String? = null,

	@field:SerializedName("cardBin")
	val cardBin: String? = null,

	@field:SerializedName("lastFourDigits")
	val lastFourDigits: String? = null,

	@field:SerializedName("gatewayref")
	val gatewayref: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("gatewayMessage")
	val gatewayMessage: String? = null,

	@field:SerializedName("redirectLink")
	val redirectLink: String? = null
)

data class QueryData(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("payments")
	val payments: Payments? = null,

	@field:SerializedName("customers")
	val customers: Customers? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Customers(

	@field:SerializedName("customerEmail")
	val customerEmail: String? = null,

	@field:SerializedName("fee")
	val fee: String? = null,

	@field:SerializedName("customerMobile")
	val customerMobile: String? = null,

	@field:SerializedName("customerName")
	val customerName: String? = null
)
