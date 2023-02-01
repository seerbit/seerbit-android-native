package com.example.seerbitsdk


interface SeerBitDestination {
    val name: String
    val attachedDescription: String
    val route: String
}

data class PaymentGatewayOptions(
    val name: String,
    val Desc: String
)


/**
 * SeerBit app navigation destinations
 */


object Debit_CreditCard : SeerBitDestination {
    override val name = "Debit/Credit Card"
    override val attachedDescription = ""
    override val route: String = "Card"


}

object BankAccount : SeerBitDestination {
    override val name = "Bank Account"
    override val attachedDescription = ""
    override val route: String = "Bank"


}
object Ussd : SeerBitDestination {
    override val name = "USSD"
    override val attachedDescription = "*bank ussd code#"
    override val route: String = "USSD"
}

object Transfer : SeerBitDestination {
    override val name = "Transfer"
    override val attachedDescription = ""
    override val route: String = "Transfer"
}

object PhoneNumber : SeerBitDestination {
    override val name = "Phone Number"
    override val attachedDescription = ""
    override val route: String = "PhoneNumber"
}

object Cash : SeerBitDestination {
    override val name = "Cash"
    override val attachedDescription = "Nearest Mobile Agent"
    override val route: String = "Cash"
}

object PaymentTypeData {

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

// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(Debit_CreditCard, BankAccount, Ussd, Transfer, PhoneNumber, Cash)
