package com.example.yolofitclient.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yolofitclient.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val PlayFairDisplay = FontFamily(
    Font(R.font.playfairdisplay_regular),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold),
    Font(R.font.playfairdisplay_italic, FontWeight.Thin)
)

val Arsenal = FontFamily(
    Font(R.font.arsenalsc_regular),
    Font(R.font.arsenalsc_bold, FontWeight.Bold),
    Font(R.font.arsenalsc_italic, FontWeight.Thin)
)


val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayFairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlayFairDisplay,
        fontWeight = FontWeight.Thin,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlayFairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
)