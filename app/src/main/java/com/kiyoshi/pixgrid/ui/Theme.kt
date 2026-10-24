package com.kiyoshi.pixgrid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Palette: gelap hangat, aksen oranye-amber, sekunder ungu lembut
val Ember = Color(0xFFFF7A45)
val Amber = Color(0xFFFFB347)
val Violet = Color(0xFF8A7CFF)
val Ink = Color(0xFF0A0A0F)
val Ink2 = Color(0xFF12121A)
val Ink3 = Color(0xFF1B1B26)
val Ink4 = Color(0xFF262633)
val Mist = Color(0xFF9A9AAE)
val Mist2 = Color(0xFF6E6E82)

private val DarkColors = darkColorScheme(
    primary = Ember,
    onPrimary = Color(0xFF1A0A00),
    primaryContainer = Color(0xFF3A1A08),
    onPrimaryContainer = Amber,
    secondary = Violet,
    onSecondary = Color(0xFF0A0A18),
    tertiary = Amber,
    onTertiary = Color(0xFF1A1000),
    background = Ink,
    onBackground = Color(0xFFF2F2F5),
    surface = Ink2,
    onSurface = Color(0xFFF2F2F5),
    surfaceVariant = Ink3,
    onSurfaceVariant = Mist,
    outline = Ink4,
    outlineVariant = Color(0xFF2E2E3D),
    error = Color(0xFFFF5A5A),
    onError = Color(0xFF2A0000),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFD65A20),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3D6),
    onPrimaryContainer = Color(0xFF3A1300),
    secondary = Color(0xFF5B4FD6),
    onSecondary = Color.White,
    tertiary = Color(0xFFB87300),
    onTertiary = Color.White,
    background = Color(0xFFFAF9FB),
    onBackground = Color(0xFF1A1A22),
    surface = Color.White,
    onSurface = Color(0xFF1A1A22),
    surfaceVariant = Color(0xFFF0EEF3),
    onSurfaceVariant = Color(0xFF5E5E70),
    outline = Color(0xFFD6D4DC),
    outlineVariant = Color(0xFFE6E4EC),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.3).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.3.sp,
    ),
)

@Composable
fun PixGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
