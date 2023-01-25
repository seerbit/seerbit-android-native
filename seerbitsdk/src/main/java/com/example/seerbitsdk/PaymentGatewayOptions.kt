package com.example.seerbitsdk

data class PaymentGatewayOptions(
    val name: String,
    val Desc: String
)


object paymentTypeData {

    val paymentData: List<PaymentGatewayOptions> = listOf(
        PaymentGatewayOptions(
            "Bank Account",
            ""
        ),
        PaymentGatewayOptions(
            "USSD",
            "*bank ussd code#"
        ),
        PaymentGatewayOptions(
            "Transfer",
            ""
        ),
        PaymentGatewayOptions(
            "Phone Number",
            ""
        ),
        PaymentGatewayOptions(
            "Cash",
            "Nearest Mobile Agent"
        )
    )
}

