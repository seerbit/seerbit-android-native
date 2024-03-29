package com.example.seerbitsdk.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.seerbitsdk.R

// Set of Material typography styles to start with

val Faktpro = FontFamily(
    Font(R.font.fakt_pro_normal)
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Faktpro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    button = TextStyle(
        fontFamily =Faktpro,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = Faktpro,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

)