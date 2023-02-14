package com.example.seerbitsdk.api

import retrofit2.http.GET

interface SeerBitService {

    @GET("/merchant/clear/")
    fun merchantCheckOut() {
    }


}