package com.example.seerbitsdk.models.ussd

data class UssdBankAccounts(
    val bankCode: String,
    val bankName: String,
    val abb: String
)


object UssdBankData {
    val ussdBank: List<UssdBankAccounts> = listOf(
        UssdBankAccounts(
            "044",
            "ACCESS BANK PLC",
            "ACCESS BANK's"),

        UssdBankAccounts(
            "063",
            "ACCESS DIAMOND PLC",
            "ACCESS DIAMOND's"),

        UssdBankAccounts(
            "050",
            "ECOBANK BANK PLC",
            "ECOBANK's"),

        UssdBankAccounts(
            "070",
            "FIDELITY BANK PLC",
            "FIDELITY BANK's"),

        UssdBankAccounts(
            "011",
            "FIRST BANK OF NIGERIA PLC",
            "FIRST BANK's"),

        UssdBankAccounts(
            "214",
            "FIRST CITY MONUMENT BANK PLC",
            "ACCESS BANK's"),

        UssdBankAccounts(
            "044",
            "ACCESS BANK PLC",
            "FCMB's"),

        UssdBankAccounts(
            "044",
            "ACCESS BANK PLC",
            "ACCESS BANK's"),

        UssdBankAccounts(
            "058",
            "Guarantee Trust Bank PLC",
            "GTBank's"),

        UssdBankAccounts(
            "082",
            "KEYSTONE BANK",
            "KEYSTONE BANK's"),

        UssdBankAccounts(
            "090175",
            "HIGHSTREET MICROFINANCE BANK",
            "HIGHSTREET's"),

        UssdBankAccounts(
            "221",
            "STANBIC IBTC BANK PLC",
            "STANBIC IBTC's"),

        UssdBankAccounts(
            "032",
            "UNION BANK OF NIGERIA PLC",
            "UNION BANK's"),

        UssdBankAccounts(
            "215",
            "UNITY BANK PLC",
            "UNITY BANK's"),

        UssdBankAccounts(
            "090110",
            "VFD MICROFINANCE BANK",
            "VFD's"),

        UssdBankAccounts(
            "035",
            "WEMA BANK PLC",
            "WEMA BANK's"),

        UssdBankAccounts(
            "057",
            "ZENITH BANK PLC",
            "ZENITH BANK's"),

        UssdBankAccounts(
            "322",
            "Zenith Mobile",
            "Zenith Mobile's"),


    )

}