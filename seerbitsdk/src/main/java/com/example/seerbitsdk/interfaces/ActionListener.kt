package com.example.seerbitsdk.interfaces

import com.example.seerbitsdk.models.query.QueryData


interface ActionListener{

    fun onSuccess(data: QueryData?)

    fun onClose()
}



