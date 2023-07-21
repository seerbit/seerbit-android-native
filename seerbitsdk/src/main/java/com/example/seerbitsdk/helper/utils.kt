package com.example.seerbitsdk.helper

import android.util.Log
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import java.math.RoundingMode
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.NumberFormat
import java.util.*

enum class TransactionType(val type: String) {
    CARD("CARD"),
    TRANSFER("TRANSFER"),
    MOMO("MOMO"),
    USSD("USSD"),
    ACCOUNT("ACCOUNT")
}

fun String.isValidCardNumber() : Boolean{
    try {
        if(this.length !in 16..20)
            return false
        val digit = this.map(Character::getNumericValue).toIntArray()
        for (i in digit.size -2 downTo 0 step 2){
            var tempValue = digit[i]
            tempValue *= 2
            if(tempValue > 9){
                tempValue = tempValue % 10 + 1
            }
            digit[i] = tempValue
        }
        var total = 0
        for (num:Int in digit){
            total +=num
        }
        return total%10 == 0
    }
    catch (e:Exception){
        return false
    }
}

fun displayPaymentMethod(paymentMethod: String, merchantDetailsResponse: MerchantDetailsResponse?): Boolean {
    if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {
        //merchant has payment configuration
        merchantDetailsResponse.payload.paymentConfigs.forEach {
            if (it?.code == paymentMethod) {
                if (merchantDetailsResponse.payload.channelOptionStatus?.isNotEmpty() == true) {
                    merchantDetailsResponse.payload.channelOptionStatus.forEach { channelOptionStatus ->
                        if (it.code == channelOptionStatus?.code && channelOptionStatus.allowOption == true && it.status == "ACTIVE") return true
                    }
                } else {
                    //channel option status is empty
                    merchantDetailsResponse.payload.paymentConfigs.forEach { paymentConfigs ->
                        if (paymentConfigs?.code == paymentMethod && paymentConfigs.allowOption == true && paymentConfigs.status == "ACTIVE") return true
                    }
                }
            }
        }
    } else {
        merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
            if (it?.code == paymentMethod) {

                if (merchantDetailsResponse.payload.channelOptionStatus?.isNotEmpty() == true) {
                    merchantDetailsResponse.payload.channelOptionStatus.forEach { channelOptionStatus ->
                        if (it.code == channelOptionStatus?.code && channelOptionStatus.allowOption == true && it.status == "ACTIVE") return true
                    }
                } else {
                    merchantDetailsResponse.payload.country.defaultPaymentOptions.forEach { defaultPaymentOptions ->
                        if (defaultPaymentOptions?.code == paymentMethod && defaultPaymentOptions.allowOption == true && defaultPaymentOptions.status == "ACTIVE") return true
                    }
                }
            }
        }
    }
    return false
}

fun generateSourceIp(useIPv4: Boolean = true): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addresses: List<InetAddress> = Collections.list(intf.getInetAddresses())
            for (address in addresses) {
                if (!address.isLoopbackAddress()) {
                    val sAddr: String = address.getHostAddress() as String
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr)
                    val isIPv4: Boolean = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4)
                            return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim: Int = sAddr.indexOf('%') // drop ip6 zone suffix
                            if (delim < 0) return sAddr.uppercase()
                            return sAddr.substring(0, delim).uppercase()
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {

    } // for now eat exceptions
    return ""
}

fun formatInputDouble(input: String?): String {
    if (input == null) return ""
    if (input.isEmpty()) return ""
    val value = input.toDouble()
    val formatter = NumberFormat.getInstance()
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    formatter.roundingMode = RoundingMode.CEILING
    return try {
        formatter.format(value)
    } catch (e: Exception) {
        "$value"
    }
}

fun calculateTransactionFee(
    merchantDetailsResponse: MerchantDetailsResponse?,
    transactionType: String,
    amount: Double,
    cardCountry: String = ""
): String? {

    if (transactionType == TransactionType.USSD.type) {
        if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {

            merchantDetailsResponse.payload.paymentConfigs.forEach {

                if (it?.code == "USSD") {
                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val cappedFee: String? =
                        it.paymentOptionCapStatus?.cappedAmount
                    val feePercentage: Double? = it.paymentOptionFee?.toDouble()

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(cappedFee.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        val fee: Double? = it.paymentOptionFee?.toDouble()
                        return formatInputDouble(fee.toString())
                    }
                }
            }

        } else {

            merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
                if (it?.code == "USSD") {

                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val feePercentage =
                        it.paymentOptionFee
                    val cappedFee: String? = it.paymentOptionCapStatus?.cappedAmount

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.toDouble()?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(it.paymentOptionCapStatus.cappedAmount.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        return it.paymentOptionFee?.let { it1 -> formatInputDouble(it1) }
                    }
                }
            }
        }

    } else if (transactionType == TransactionType.TRANSFER.type) {

        if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {

            merchantDetailsResponse.payload.paymentConfigs.forEach {

                if (it?.code == "TRANSFER") {
                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val cappedFee: String? =
                        it.paymentOptionCapStatus?.cappedAmount
                    val feePercentage: Double? = it.paymentOptionFee?.toDouble()

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(cappedFee.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        val fee: Double? = it.paymentOptionFee?.toDouble()
                        return formatInputDouble(fee.toString())
                    }
                }
            }

        } else {

            merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
                if (it?.code == "TRANSFER") {

                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val feePercentage =
                        it.paymentOptionFee
                    val cappedFee: String? = it.paymentOptionCapStatus?.cappedAmount

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.toDouble()?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(it.paymentOptionCapStatus.cappedAmount.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        return it.paymentOptionFee?.let { it1 -> formatInputDouble(it1) }
                    }
                }
            }
        }

    } else if (transactionType == TransactionType.CARD.type) {

        if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {

            merchantDetailsResponse.payload.paymentConfigs.forEach {

                if (it?.code == "CARD") {
                    val isCardInternational: Boolean =
                        merchantDetailsResponse.payload.country?.nameCode?.let { it1 ->
                            cardCountry.contains(
                                it1, true
                            )
                        } == false
                    Log.d("sdad", "${it.internationalPaymentOptionCapStatus} xzxx")
                    if (isCardInternational) {
                        // use international charges
                        val feeModeIsPercentage: Boolean =
                            it.internationalPaymentOptionMode == "PERCENTAGE"
                        val isCappedSettlement: Boolean =
                            it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                        val cappedFee: String? =
                            it.internationalPaymentOptionCapStatus?.inCappedAmount?.toString()

                        val feePercentage: Double? = it.internationalPaymentOptionFee?.toDouble()

                        if (feeModeIsPercentage) {
                            val fee = (feePercentage?.div(100.00))?.times(amount)
                            if (isCappedSettlement) {
                                if (fee != null && cappedFee != null) {
                                    if (fee > cappedFee.toDouble()) {
                                        return formatInputDouble(cappedFee.toString())
                                    } else {
                                        return formatInputDouble(fee.toString())
                                    }
                                } else {
                                    Log.d("sdad", "$fee $cappedFee  qwer")
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            val fee: Double? = it.paymentOptionFee?.toDouble()
                            return formatInputDouble(fee.toString())
                        }

                    } else {
                        val feeModeIsPercentage: Boolean =
                            it.paymentOptionFeeMode == "PERCENTAGE"
                        val isCappedSettlement: Boolean =
                            it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                        val cappedFee: String? =
                            it.paymentOptionCapStatus?.cappedAmount
                        val feePercentage: Double? = it.paymentOptionFee?.toDouble()

                        if (feeModeIsPercentage) {
                            val fee = (feePercentage?.div(100.00))?.times(amount)
                            if (isCappedSettlement) {
                                if (fee != null && cappedFee != null) {
                                    if (fee > cappedFee.toDouble()) {
                                        return formatInputDouble(cappedFee.toString())
                                    } else {
                                        return formatInputDouble(fee.toString())
                                    }
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            val fee: Double? = it.paymentOptionFee?.toDouble()
                            return formatInputDouble(fee.toString())
                        }

                    }

                }
            }

        } else {
            merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
                if (it?.code == "CARD") {
                    val isCardInternational: Boolean =
                        merchantDetailsResponse.payload.country.nameCode?.let { it1 ->
                            cardCountry.contains(
                                it1, true
                            )
                        } == false

                    if (isCardInternational) {
                        // use international charges
                        val feeModeIsPercentage: Boolean =
                            it.internationalPaymentOptionMode == "PERCENTAGE"
                        val isCappedSettlement: Boolean =
                            it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                        val cappedFee: String? =
                            it.internationalPaymentOptionCapStatus?.inCappedAmount?.toString()

                        val feePercentage: Double? = it.internationalPaymentOptionFee?.toDouble()

                        if (feeModeIsPercentage) {
                            val fee = (feePercentage?.div(100.00))?.times(amount)
                            if (isCappedSettlement) {
                                if (fee != null && cappedFee != null) {
                                    if (fee > cappedFee.toDouble()) {
                                        return formatInputDouble(cappedFee.toString())
                                    } else {
                                        return formatInputDouble(fee.toString())
                                    }
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            val fee: Double? = it.paymentOptionFee?.toDouble()
                            return formatInputDouble(fee.toString())
                        }

                    } else {

                        val feeModeIsPercentage: Boolean =
                            it.paymentOptionFeeMode == "PERCENTAGE"
                        val isCappedSettlement: Boolean =
                            it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                        val feePercentage =
                            it.paymentOptionFee
                        val cappedFee: String? = it.paymentOptionCapStatus?.cappedAmount

                        if (feeModeIsPercentage) {
                            val fee = (feePercentage?.toDouble()?.div(100.00))?.times(amount)
                            if (isCappedSettlement) {
                                if (fee != null && cappedFee != null) {
                                    if (fee > cappedFee.toDouble()) {
                                        return formatInputDouble(it.paymentOptionCapStatus.cappedAmount.toString())
                                    } else {
                                        return formatInputDouble(fee.toString())
                                    }
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble(fee.toString())
                            }
                        } else {
                            return it.paymentOptionFee?.let { it1 -> formatInputDouble(it1) }
                        }

                    }
                }
            }
        }

    } else if (transactionType == TransactionType.ACCOUNT.type) {

        if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {

            merchantDetailsResponse.payload.paymentConfigs.forEach {

                if (it?.code == "ACCOUNT") {
                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val cappedFee: String? =
                        it.paymentOptionCapStatus?.cappedAmount
                    val feePercentage: Double? = it.paymentOptionFee?.toDouble()

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(cappedFee.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble("")
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        val fee: Double? = it.paymentOptionFee?.toDouble()
                        return formatInputDouble(fee.toString())
                    }
                }
            }

        } else {

            merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
                if (it?.code == "ACCOUNT") {

                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val feePercentage =
                        it.paymentOptionFee
                    val cappedFee: String? = it.paymentOptionCapStatus?.cappedAmount

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.toDouble()?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(it.paymentOptionCapStatus.cappedAmount.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble("")
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        return it.paymentOptionFee?.let { it1 -> formatInputDouble(it1) }
                    }
                }
            }
        }


    } else if (transactionType == TransactionType.MOMO.type) {

        if (merchantDetailsResponse?.payload?.paymentConfigs?.isNotEmpty() == true) {

            merchantDetailsResponse.payload.paymentConfigs.forEach {

                if (it?.code == "MOMO") {
                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val cappedFee: String? =
                        it.paymentOptionCapStatus?.cappedAmount
                    val feePercentage: Double? = it.paymentOptionFee?.toDouble()

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(cappedFee.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble("")
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        val fee: Double? = it.paymentOptionFee?.toDouble()
                        return formatInputDouble(fee.toString())
                    }
                }
            }

        } else {

            merchantDetailsResponse?.payload?.country?.defaultPaymentOptions?.forEach {
                if (it?.code == "MOMO") {

                    val feeModeIsPercentage: Boolean =
                        it.paymentOptionFeeMode == "PERCENTAGE"
                    val isCappedSettlement: Boolean =
                        it.paymentOptionCapStatus?.cappedSettlement == "CAPPED"
                    val feePercentage =
                        it.paymentOptionFee
                    val cappedFee: String? = it.paymentOptionCapStatus?.cappedAmount

                    if (feeModeIsPercentage) {
                        val fee = (feePercentage?.toDouble()?.div(100.00))?.times(amount)
                        if (isCappedSettlement) {
                            if (fee != null && cappedFee != null) {
                                if (fee > cappedFee.toDouble()) {
                                    return formatInputDouble(it.paymentOptionCapStatus.cappedAmount.toString())
                                } else {
                                    return formatInputDouble(fee.toString())
                                }
                            } else {
                                return formatInputDouble("")
                            }
                        } else {
                            return formatInputDouble(fee.toString())
                        }
                    } else {
                        return it.paymentOptionFee?.let { it1 -> formatInputDouble(it1) }
                    }
                }
            }
        }
    }

    return formatInputDouble("")
}

fun isMerchantFeeBearer(merchantDetailsResponse: MerchantDetailsResponse?): Boolean {
    return merchantDetailsResponse?.payload?.setting?.chargeOption == "MERCHANT"
}