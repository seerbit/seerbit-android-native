package com.example.seerbitsdk.repository

import android.util.Log
import com.example.seerbitsdk.api.OTPService
import com.example.seerbitsdk.api.OtpApiService
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.OtpDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.otp.MomoOtpDto
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import retrofit2.Response

class OTPRepository {

    suspend fun sendOtp(otpDTO: OtpDTO): Response<CardResponse> {
        return OtpApiService.retrofitService.sendOtp(otpDTO)
    }

    suspend fun sendOtpForBankAccount(otpDTO: OtpDTO): Response<CardResponse> {
        return OtpApiService.retrofitService.sendOtpForBankAccount(otpDTO)
    }

    suspend fun sendOtpMomo(otpDTO: MomoOtpDto): Response<CardResponse> {
        return OtpApiService.retrofitService.sendOtpMomo(otpDTO)
    }


}