package com.example.seerbitsdk.api

import com.example.seerbitsdk.models.InitiateCardDTO
import retrofit2.http.POST

interface CardService {

    @POST("/sandbox/initiates")
    fun initiateCardPaymentMethod(cardDTO: InitiateCardDTO)

}