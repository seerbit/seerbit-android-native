package com.example.seerbitsdk

import android.net.Uri
import androidx.core.net.toUri


interface SeerBitDestination {
    val name: String
    val attachedDescription: String
    val route: String
}

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

object UssdSelectBank : SeerBitDestination {
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

object MOMO : SeerBitDestination {
    override val name = "MOMO"
    override val attachedDescription = "Nearest Mobile Agent"
    override val route: String = "Cash"
}

// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(Debit_CreditCard, BankAccount, UssdSelectBank, Transfer)

object DeepLinkPattern{
    private val BaseUri = "https://com.example.seerbit_sdk".toUri()
    val HomePattern = "$BaseUri"
    fun getHomeUri() : Uri = HomePattern.toUri()
}
