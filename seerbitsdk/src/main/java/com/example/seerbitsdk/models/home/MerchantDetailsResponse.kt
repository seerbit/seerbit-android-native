package com.example.seerbitsdk.models.home

import com.google.gson.annotations.SerializedName

data class MerchantDetailsResponse(

	@SerializedName("payload")
	val payload: Payload? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("responseCode")
	val responseCode: String? = null
)

data class DefaultCurrency(

	@SerializedName("country")
	val country: String? = null,

	@SerializedName("number")
	val number: String? = null,

	@SerializedName("code")
	val code: String? = null,

	@SerializedName("currency")
	val currency: String? = null
)

data class ChannelOptionStatusItem(

	@SerializedName("allow_option")
	val allowOption: Boolean? = null,

	@SerializedName("name")
	val name: String? = null
)

data class DefaultFee(

	@SerializedName("mccCategory")
	val mccCategory: String? = null,

	@SerializedName("mccPercentage")
	val mccPercentage: String? = null
)

data class Country(

	@SerializedName("continent")
	val continent: String? = null,

	@SerializedName("nameCode")
	val nameCode: String? = null,

	@SerializedName("websiteUrl")
	val websiteUrl: String? = null,

	@SerializedName("countryCode")
	val countryCode: String? = null,

	@SerializedName("defaultCurrency")
	val defaultCurrency: DefaultCurrency? = null,

	@SerializedName("defaultPaymentOptions")
	val defaultPaymentOptions: List<DefaultPaymentOptionsItem?>? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("countryCode2")
	val countryCode2: String? = null,

	@SerializedName("status")
	val status: String? = null
)

data class Payload(

	@SerializedName("country")
	val country: Country? = null,

	@SerializedName("enableUnderCharge")
	val enableUnderCharge: Boolean? = null,

	@SerializedName("checkoutPageConfig")
	val checkoutPageConfig: CheckoutPageConfig? = null,

	@SerializedName("setting")
	val setting: Setting? = null,

	@SerializedName("number")
	val number: String? = null,

	@SerializedName("paymentConfigs")
	val paymentConfigs: List<Any?>? = null,

	@SerializedName("cardFee")
	val cardFee: CardFee? = null,

	@SerializedName("channelOptionStatus")
	val channelOptionStatus: List<ChannelOptionStatusItem?>? = null,

	@SerializedName("live_public_key")
	val livePublicKey: String? = null,

	@SerializedName("logo")
	val logo: String? = null,

	@SerializedName("max_threshold")
	val maxThreshold: String? = null,

	@SerializedName("business_name")
	val businessName: String? = null,

	@SerializedName("address")
	val address: Address? = null,

	@SerializedName("min_amount")
	val minAmount: Double? = null,

	@SerializedName("isWhiteLabelled")
	val isWhiteLabelled: Boolean? = null,

	@SerializedName("transLink")
	val transLink: String? = null,

	@SerializedName("min_threshold")
	val minThreshold: String? = null,

	@SerializedName("enableOvercharge")
	val enableOvercharge: Boolean? = null,

	@SerializedName("enableForDiscount")
	val enableForDiscount: Boolean? = null,

	@SerializedName("transactionFee")
	val transactionFee: TransactionFee? = null,

	@SerializedName("support_email")
	val supportEmail: String? = null,

	@SerializedName("activeForValidationService")
	val activeForValidationService: Boolean? = null,

	@SerializedName("allowedCurrency")
	val allowedCurrency: List<String?>? = null,

	@SerializedName("enable_descriptor")
	val enableDescriptor: Boolean? = null,

	@SerializedName("test_public_key")
	val testPublicKey: String? = null,

	@SerializedName("max_amount")
	val maxAmount: Double? = null,

	@SerializedName("partnerId")
	val partnerId: String? = null,

	@SerializedName("default_currency")
	val defaultCurrency: String? = null,

	@SerializedName("enableCustomerForTransfer")
	val enableCustomerForTransfer: Boolean? = null,

	@SerializedName("status")
	val status: String? = null
)

data class Setting(

	@SerializedName("mode")
	val mode: String? = null,

	@SerializedName("transfer_option")
	val transferOption: Boolean? = null,

	@SerializedName("email_receipt_merchant")
	val emailReceiptMerchant: Boolean? = null,

	@SerializedName("applySettlementPattern")
	val applySettlementPattern: Boolean? = null,

	@SerializedName("bank_option")
	val bankOption: Boolean? = null,

	@SerializedName("email_receipt_customer")
	val emailReceiptCustomer: Boolean? = null,

	@SerializedName("card_option")
	val cardOption: Boolean? = null,

	@SerializedName("display_fee")
	val displayFee: Boolean? = null,

	@SerializedName("payday")
	val payday: String? = null,

	@SerializedName("charge_option")
	val chargeOption: String? = null
)

data class PaymentOptionCapStatus(

	@SerializedName("cappedAmount")
	val cappedAmount: Double? = null,

	@SerializedName("cappedSettlement")
	val cappedSettlement: String? = null
)

data class AccountTransactionFee(
	val any: Any? = null
)

data class CheckoutPageConfig(

	@SerializedName("paybuttonColor")
	val paybuttonColor: String? = null,

	@SerializedName("backgroundColor")
	val backgroundColor: String? = null,

	@SerializedName("checkoutAdverts")
	val checkoutAdverts: List<Any?>? = null,

	@SerializedName("maxCheckAdvertCount")
	val maxCheckAdvertCount: String? = null,

	@SerializedName("paychannelColor")
	val paychannelColor: String? = null,

	@SerializedName("checkAdvertStatus")
	val checkAdvertStatus: String? = null
)

data class TransactionCapStatus(

	@SerializedName("cappedAmount")
	val cappedAmount: String? = null,

	@SerializedName("cappedSettlement")
	val cappedSettlement: String? = null
)

data class InternationalPaymentOptionCapStatus(

	@SerializedName("inCappedAmount")
	val inCappedAmount: Double? = null
)

data class CardFee(

	@SerializedName("mc")
	val mc: String? = null,

	@SerializedName("visa")
	val visa: String? = null,

	@SerializedName("verve")
	val verve: String? = null
)

data class DefaultPaymentOptionsItem(

	@SerializedName("paymentOptionFeeMode")
	val paymentOptionFeeMode: String? = null,

	@SerializedName("internationalPaymentOptionFee")
	val internationalPaymentOptionFee: Double? = null,

	@SerializedName("internationalPaymentOptionCapStatus")
	val internationalPaymentOptionCapStatus: InternationalPaymentOptionCapStatus? = null,

	@SerializedName("code")
	val code: String? = null,

	@SerializedName("internationalPaymentOptionMode")
	val internationalPaymentOptionMode: String? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("type")
	val type: String? = null,

	@SerializedName("paymentOptionFee")
	val paymentOptionFee: String? = null,

	@SerializedName("number")
	val number: String? = null,

	@SerializedName("allow_option")
	val allowOption: Boolean? = null,

	@SerializedName("viewName")
	val viewName: String? = null,

	@SerializedName("paymentOptionCapStatus")
	val paymentOptionCapStatus: PaymentOptionCapStatus? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("applyFixCharge")
	val applyFixCharge: Boolean? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("fixCharge")
	val fixCharge: String? = null
)

data class TransactionFee(

	@SerializedName("transactionCapStatus")
	val transactionCapStatus: TransactionCapStatus? = null,

	@SerializedName("defaultFee")
	val defaultFee: DefaultFee? = null,

	@SerializedName("accountTransactionFee")
	val accountTransactionFee: AccountTransactionFee? = null,

	@SerializedName("cardTransactionFee")
	val cardTransactionFee: CardTransactionFee? = null
)

data class Address(

	@SerializedName("country")
	val country: String? = null,

	@SerializedName("city")
	val city: String? = null,

	@SerializedName("street")
	val street: String? = null,

	@SerializedName("state")
	val state: String? = null
)

data class CardTransactionFee(
	val any: Any? = null
)
