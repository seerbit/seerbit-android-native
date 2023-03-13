package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.models.ussd.UssdDTO
import com.example.seerbitsdk.screenstate.CardBinState
import com.example.seerbitsdk.use_cases.InitiateUseCase
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.use_cases.CardBinUseCase
import com.example.seerbitsdk.use_cases.OtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionViewModel : ViewModel() {

    private val initiateTransactionUseCase: InitiateUseCase = InitiateUseCase()
    private val sendOtpUseCase: OtpUseCase = OtpUseCase()
    private val cardBinUseCase: CardBinUseCase = CardBinUseCase()

    private var _initiateTransactionState = mutableStateOf(InitiateTransactionState())
    val initiateTransactionState: State<InitiateTransactionState>
        get() = _initiateTransactionState


    private var _queryTransactionState = mutableStateOf(QueryTransactionState())
    val queryTransactionState: State<QueryTransactionState>
        get() = _queryTransactionState


    private var _otpState = mutableStateOf(OTPState())
    val otpState: State<OTPState>
        get() = _otpState

    private var _cardBinState = mutableStateOf(CardBinState())
    val cardBinState: State<CardBinState>
        get() = _cardBinState

    private var _paymentRefState = mutableStateOf("")
    val paymentRefState: State<String>
        get() = _paymentRefState


    private var _initiateTransactionState2 = mutableStateOf(InitiateTransactionState())
    val initiateTransactionState2: State<InitiateTransactionState>
        get() = _initiateTransactionState2


    init {
        clearCardBinState()
        resetTransactionState()
        _paymentRefState.value = generateRandomReference()
    }

    fun resetTransactionState() {
        _initiateTransactionState.value = InitiateTransactionState()
        _initiateTransactionState2.value = InitiateTransactionState()
        _queryTransactionState.value = QueryTransactionState()
        _otpState.value = OTPState()
    }


    fun clearCardBinState() {
        _cardBinState.value = CardBinState()
    }

    fun initiateTransaction(transactionDTO: TransactionDTO) {
        _initiateTransactionState.value = InitiateTransactionState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {
            initiateTransactionUseCase(transactionDTO).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _initiateTransactionState.value =
                            InitiateTransactionState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _initiateTransactionState.value = InitiateTransactionState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _initiateTransactionState.value = InitiateTransactionState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun initiateUssdTransaction(ussdDTO: UssdDTO) {
        _initiateTransactionState2.value = InitiateTransactionState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            initiateTransactionUseCase(ussdDTO).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _initiateTransactionState2.value =
                            InitiateTransactionState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _initiateTransactionState2.value = InitiateTransactionState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _initiateTransactionState2.value = InitiateTransactionState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }


    fun queryTransaction(paymentReference: String) {
        _queryTransactionState.value = QueryTransactionState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            initiateTransactionUseCase(paymentReference = paymentReference).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _queryTransactionState.value = QueryTransactionState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _queryTransactionState.value = QueryTransactionState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _queryTransactionState.value = QueryTransactionState(
                            isLoading = true
                        )
                    }
                }
            }

        }
    }


    fun sendOtp(cardOTPDTO: CardOTPDTO) {
        _otpState.value = OTPState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            sendOtpUseCase(cardOTPDTO).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _otpState.value = OTPState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _otpState.value = OTPState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _queryTransactionState.value = QueryTransactionState(
                            isLoading = true
                        )
                    }
                }
            }

        }
    }


    fun fetchCardBin(firstSixDigits: String) {
        _cardBinState.value = CardBinState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            cardBinUseCase(firstSixDigits).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _cardBinState.value = CardBinState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _cardBinState.value = CardBinState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _cardBinState.value = CardBinState(
                            isLoading = true
                        )
                    }
                }
            }
        }

    }




    fun getPaymentReference(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        return "SBT-T${current.format(formatter)}"
    }

    fun generateRandomReference(): String {

        val str = "ABCDEFGHIJKLMNOPQRSTNVabcdef6ghijklmnopqrstuvwxyzABCD123456789"
        var password = ""
        for (i in 1..8) {
            password += str.random()
        }
        return "SBT-T" + UUID.randomUUID().toString()
    }


}