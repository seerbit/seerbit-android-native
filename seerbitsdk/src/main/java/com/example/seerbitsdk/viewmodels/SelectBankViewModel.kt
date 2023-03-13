package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.screenstate.*
import com.example.seerbitsdk.use_cases.GetBanksUseCase
import com.example.seerbitsdk.use_cases.GetMomoNetworkUseCase
import com.example.seerbitsdk.use_cases.InitiateUseCase
import com.example.seerbitsdk.use_cases.OtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectBankViewModel: ViewModel() {

    private val getBanksUseCase : GetBanksUseCase = GetBanksUseCase()

    private var _availableBanksState = mutableStateOf(AvailableBanksState())
    val availableBanksState: State<AvailableBanksState>
        get() = _availableBanksState

    private val getMomoNetworkUseCase : GetMomoNetworkUseCase = GetMomoNetworkUseCase()

    private var _momoNetworkState = mutableStateOf(MomoNetworkState())
    val momoNetworkState : State<MomoNetworkState>
        get() = _momoNetworkState


    init {
        getBanks()
        //getMomoNetworks()
    }

     fun resetTransactionState() {
        _availableBanksState.value = AvailableBanksState()
    }




     fun getBanks() {
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

    fun getMomoNetworks() {
        _momoNetworkState.value = MomoNetworkState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {
            getMomoNetworkUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _momoNetworkState.value =
                            MomoNetworkState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _momoNetworkState.value = MomoNetworkState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _momoNetworkState.value = MomoNetworkState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
}
