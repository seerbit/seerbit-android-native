package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.screenstate.AvailableBanksState
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.OTPState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import com.example.seerbitsdk.use_cases.GetBanksUseCase
import com.example.seerbitsdk.use_cases.InitiateUseCase
import com.example.seerbitsdk.use_cases.OtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectBankViewModel: ViewModel() {

    private val getBanksUseCase : GetBanksUseCase = GetBanksUseCase()

    private var _availableBanksState = mutableStateOf(AvailableBanksState())
    val availableBanksState: State<AvailableBanksState>
        get() = _availableBanksState


    init {
        getBanks()
    }

    fun resetTransactionState() {
        _availableBanksState.value = AvailableBanksState()
    }




    private fun getBanks() {
        _availableBanksState.value = AvailableBanksState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {
            getBanksUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _availableBanksState.value =
                            AvailableBanksState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _availableBanksState.value = AvailableBanksState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _availableBanksState.value = AvailableBanksState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
}
