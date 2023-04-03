package com.example.seerbitsdk.viewmodels


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MerchatDetailVmFactory(
    private val pulicKey: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("unchecked cast")
        if (modelClass.isAssignableFrom(MerchantDetailsViewModel::class.java)) {
            return MerchantDetailsViewModel(pulicKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
