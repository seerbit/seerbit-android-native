package com.example.seerbitsdk.models.home

import com.google.gson.annotations.SerializedName

data class MerchantDetailsResponse(

	@field:SerializedName("payload")
	val payload: Payload? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null
)

data class DefaultPaymentOptionsItem(

	@field:SerializedName("paymentOptionFeeMode")
	val paymentOptionFeeMode: String? = null,

	@field:SerializedName("internationalPaymentOptionFee")
	val internationalPaymentOptionFee: String? = null,

	@field:SerializedName("internationalPaymentOptionCapStatus")
	val internationalPaymentOptionCapStatus: InternationalPaymentOptionCapStatus? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("cappedAmount")
	val cappedAmount: String? = null,

	@field:SerializedName("internationalPaymentOptionMode")
	val internationalPaymentOptionMode: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("inCappedSettlement")
	val inCappedSettlement: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("cappedSettlement")
	val cappedSettlement: String? = null,

	@field:SerializedName("paymentOptionFee")
	val paymentOptionFee: String? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("allow_option")
	val allowOption: Boolean? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("viewName")
	val viewName: String? = null,

	@field:SerializedName("paymentOptionCapStatus")
	val paymentOptionCapStatus: PaymentOptionCapStatus? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("applyFixCharge")
	val applyFixCharge: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("inCappedAmount")
	val inCappedAmount: Double? = null,

	@field:SerializedName("fixCharge")
	val fixCharge: String? = null
)

data class Address(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("street")
	val street: String? = null,

	@field:SerializedName("state")
	val state: String? = null
)

data class Setting(

	@field:SerializedName("mode")
	val mode: String? = null,

	@field:SerializedName("transfer_option")
	val transferOption: Boolean? = null,

	@field:SerializedName("email_receipt_merchant")
	val emailReceiptMerchant: Boolean? = null,

	@field:SerializedName("applySettlementPattern")
	val applySettlementPattern: Boolean? = null,

	@field:SerializedName("bank_option")
	val bankOption: Boolean? = null,

	@field:SerializedName("email_receipt_customer")
	val emailReceiptCustomer: Boolean? = null,

	@field:SerializedName("card_option")
	val cardOption: Boolean? = null,

	@field:SerializedName("display_fee")
	val displayFee: Boolean? = null,

	@field:SerializedName("payday")
	val payday: String? = null,

	@field:SerializedName("charge_option")
	val chargeOption: String? = null
)

data class Invoice(

	@field:SerializedName("liveMultiBusinessBranchRef")
	val liveMultiBusinessBranchRef: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("totalLength")
	val totalLength: Int? = null,

	@field:SerializedName("testMultiBusinessBranchRef")
	val testMultiBusinessBranchRef: String? = null,

	@field:SerializedName("billLength")
	val billLength: Int? = null,

	@field:SerializedName("activeOverInternet")
	val activeOverInternet: Boolean? = null
)

data class DefaultFee(

	@field:SerializedName("mccCategory")
	val mccCategory: String? = null,

	@field:SerializedName("mccPercentage")
	val mccPercentage: String? = null
)

data class PaymentConfigsItem(

	@field:SerializedName("paymentOptionFeeMode")
	val paymentOptionFeeMode: String? = null,

	@field:SerializedName("internationalPaymentOptionFee")
	val internationalPaymentOptionFee: Double? = null,

	@field:SerializedName("internationalPaymentOptionCapStatus")
	val internationalPaymentOptionCapStatus: InternationalPaymentOptionCapStatus? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("channelOptionNumber")
	val channelOptionNumber: String? = null,

	@field:SerializedName("internationalPaymentOptionMode")
	val internationalPaymentOptionMode: String? = null,

	@field:SerializedName("inCappedSettlement")
	val inCappedSettlement: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("cappedSettlement")
	val cappedSettlement: String? = null,

	@field:SerializedName("paymentOptionFee")
	val paymentOptionFee: Double? = null,

	@field:SerializedName("allow_option")
	val allowOption: Boolean? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("viewName")
	val viewName: String? = null,

	@field:SerializedName("paymentOptionCapStatus")
	val paymentOptionCapStatus: PaymentOptionCapStatus? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("applyFixCharge")
	val applyFixCharge: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("cappedAmount")
	val cappedAmount: String? = null,

	@field:SerializedName("inCappedAmount")
	val inCappedAmount: Double? = null
)

data class CardFee(

	@field:SerializedName("mc")
	val mc: Any? = null,

	@field:SerializedName("visa")
	val visa: Any? = null,

	@field:SerializedName("verve")
	val verve: Any? = null
)

data class DefaultCurrency(

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class CheckoutPageConfig(

	@field:SerializedName("checkoutAdverts")
	val checkoutAdverts: List<Any?>? = null,

	@field:SerializedName("maxCheckAdvertCount")
	val maxCheckAdvertCount: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("checkAdvertStatus")
	val checkAdvertStatus: String? = null
)

data class CardFeeTemp(
	val any: Any? = null
)

data class TransferTransactionFee(

	@field:SerializedName("transferFee")
	val transferFee: String? = null,

	@field:SerializedName("transferFeeMode")
	val transferFeeMode: String? = null
)

data class AccountTransactionFee(

	@field:SerializedName("accountFee")
	val accountFee: String? = null,

	@field:SerializedName("accountFeeMode")
	val accountFeeMode: String? = null
)

data class TransactionCapStatus(

	@field:SerializedName("cappedAmount")
	val cappedAmount: String? = null,

	@field:SerializedName("cappedSettlement")
	val cappedSettlement: String? = null
)

data class Webhook(

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("testUrl")
	val testUrl: String? = null,

	@field:SerializedName("testUrlActive")
	val testUrlActive: Boolean? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class ChannelOptionStatusItem(

	@field:SerializedName("allow_option")
	val allowOption: Boolean? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("channelOptionNumber")
	val channelOptionNumber: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Country(

	@field:SerializedName("continent")
	val continent: String? = null,

	@field:SerializedName("default_currency_id")
	val defaultCurrencyId: Int? = null,

	@field:SerializedName("vat")
	val vat: Double? = null,

	@field:SerializedName("countryCode2")
	val countryCode2: String? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("nameCode")
	val nameCode: String? = null,

	@field:SerializedName("websiteUrl")
	val websiteUrl: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("defaultCurrency")
	val defaultCurrency: DefaultCurrency? = null,

	@field:SerializedName("defaultPaymentOptions")
	val defaultPaymentOptions: List<DefaultPaymentOptionsItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Payload(

	@field:SerializedName("country")
	val country: Country? = null,

	@field:SerializedName("merchant_email")
	val merchantEmail: String? = null,

	@field:SerializedName("webhook")
	val webhook: Webhook? = null,

	@field:SerializedName("checkoutPageConfig")
	val checkoutPageConfig: CheckoutPageConfig? = null,

	@field:SerializedName("applyVat")
	val applyVat: Boolean? = null,

	@field:SerializedName("setting")
	val setting: Setting? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("enabledForVerve3DS")
	val enabledForVerve3DS: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("canRaiseRefund")
	val canRaiseRefund: Boolean? = null,

	@field:SerializedName("settleToWallet")
	val settleToWallet: Boolean? = null,

	@field:SerializedName("kycUpdateRequired")
	val kycUpdateRequired: Boolean? = null,

	@field:SerializedName("max_threshold")
	val maxThreshold: String? = null,

	@field:SerializedName("business_name")
	val businessName: String? = null,

	@field:SerializedName("chargeback_email")
	val chargebackEmail: String? = null,

	@field:SerializedName("min_amount")
	val minAmount: Double? = null,

	@field:SerializedName("business_industry")
	val businessIndustry: String? = null,

	@field:SerializedName("isWhiteLabelled")
	val isWhiteLabelled: Boolean? = null,

	@field:SerializedName("min_threshold")
	val minThreshold: String? = null,

	@field:SerializedName("enableForDiscount")
	val enableForDiscount: Boolean? = null,

	@field:SerializedName("enforceCvv")
	val enforceCvv: Boolean? = null,

	@field:SerializedName("business_email")
	val businessEmail: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("live_private_key")
	val livePrivateKey: String? = null,

	@field:SerializedName("enableCustomerForTransfer")
	val enableCustomerForTransfer: Boolean? = null,

	@field:SerializedName("country_id")
	val countryId: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("rc_number")
	val rcNumber: String? = null,

	@field:SerializedName("enableUnderCharge")
	val enableUnderCharge: Boolean? = null,

	@field:SerializedName("applyOperationFixCharge")
	val applyOperationFixCharge: Boolean? = null,

	@field:SerializedName("paymentConfigs")
	val paymentConfigs: List<PaymentConfigsItem?>? = null,

	@field:SerializedName("cardFee")
	val cardFee: CardFee? = null,

	@field:SerializedName("channelOptionStatus")
	val channelOptionStatus: List<ChannelOptionStatusItem?>? = null,

	@field:SerializedName("live_public_key")
	val livePublicKey: String? = null,

	@field:SerializedName("canBrandCheckout")
	val canBrandCheckout: Boolean? = null,

	@field:SerializedName("address")
	val address: Address? = null,

	@field:SerializedName("test_private_key")
	val testPrivateKey: String? = null,

	@field:SerializedName("transLink")
	val transLink: String? = null,

	@field:SerializedName("enableOvercharge")
	val enableOvercharge: Boolean? = null,

	@field:SerializedName("cardFeeTemp")
	val cardFeeTemp: CardFeeTemp? = null,

	@field:SerializedName("transactionFee")
	val transactionFee: TransactionFee? = null,

	@field:SerializedName("support_email")
	val supportEmail: String? = null,

	@field:SerializedName("website_url")
	val websiteUrl: String? = null,

	@field:SerializedName("activeForValidationService")
	val activeForValidationService: Boolean? = null,

	@field:SerializedName("allowedCurrency")
	val allowedCurrency: List<String?>? = null,

	@field:SerializedName("vatFee")
	val vatFee: String? = null,

	@field:SerializedName("enable_descriptor")
	val enableDescriptor: Boolean? = null,

	@field:SerializedName("test_public_key")
	val testPublicKey: String? = null,

	@field:SerializedName("max_amount")
	val maxAmount: Double? = null,

	@field:SerializedName("applyChannelFixCharge")
	val applyChannelFixCharge: Boolean? = null,

	@field:SerializedName("SPayoutIncompleteDetailsNotification")
	val sPayoutIncompleteDetailsNotification: Boolean? = null,

	@field:SerializedName("invoice")
	val invoice: Invoice? = null,

	@field:SerializedName("partnerId")
	val partnerId: String? = null,

	@field:SerializedName("default_currency")
	val defaultCurrency: String? = null,

	@field:SerializedName("businessType")
	val businessType: String? = null,

	var userFullName : String? = null,
	var publicKey : String? = null,
	var userPhoneNumber : String? = null,
	var amount : String? = null,
    var emailAddress : String? = null
)

data class InternationalPaymentOptionCapStatus(

	@field:SerializedName("inCappedAmount")
	val inCappedAmount: Double? = null
)

data class CardTransactionFee(

	@field:SerializedName("internationalCardFee")
	val internationalCardFee: String? = null,

	@field:SerializedName("cardFee")
	val cardFee: String? = null,

	@field:SerializedName("cardFeeMode")
	val cardFeeMode: String? = null,

	@field:SerializedName("internationalCardFeeMode")
	val internationalCardFeeMode: String? = null
)

data class PaymentOptionCapStatus(

	@field:SerializedName("cappedAmount")
	val cappedAmount: String? = null,

	@field:SerializedName("cappedSettlement")
	val cappedSettlement: String? = null
)

data class TransactionFee(

	@field:SerializedName("transactionCapStatus")
	val transactionCapStatus: TransactionCapStatus? = null,

	@field:SerializedName("defaultFee")
	val defaultFee: DefaultFee? = null,

	@field:SerializedName("transferTransactionFee")
	val transferTransactionFee: TransferTransactionFee? = null,

	@field:SerializedName("accountTransactionFee")
	val accountTransactionFee: AccountTransactionFee? = null,

	@field:SerializedName("cardTransactionFee")
	val cardTransactionFee: CardTransactionFee? = null
)
