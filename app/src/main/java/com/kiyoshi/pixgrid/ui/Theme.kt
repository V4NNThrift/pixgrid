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

/*
 * Palette mengikuti Apple Human Interface Guidelines (System Colors).
 * Nilai hex diambil dari spesifikasi resmi, bukan tebakan.
 */

// --- System accent ---
val AppleBlue = Color(0xFF007AFF)
val AppleBlueDark = Color(0xFF0A84FF)
val AppleIndigo = Color(0xFF5856D6)
val ApplePurple = Color(0xFFAF52DE)
val ApplePink = Color(0xFFFF2D55)
val AppleRed = Color(0xFFFF3B30)
val AppleOrange = Color(0xFFFF9500)
val AppleYellow = Color(0xFFFFCC00)
val AppleGreen = Color(0xFF34C759)
val AppleTeal = Color(0xFF30B0C7)
val AppleMint = Color(0xFF00C7BE)

// --- macOS light ---
val WindowLight = Color(0xFFF5F5F7)
val SidebarLight = Color(0xFFEBEBED)
val CardLight = Color(0xFFFFFFFF)
val SeparatorLight = Color(0xFFD8D8DC)
val TextPrimaryLight = Color(0xFF1D1D1F)
val TextSecondaryLight = Color(0xFF6E6E73)
val TextTertiaryLight = Color(0xFFAEAEB2)

// --- macOS dark ---
val WindowDark = Color(0xFF1C1C1E)
val SidebarDark = Color(0xFF2C2C2E)
val CardDark = Color(0xFF2C2C2E)
val CardDarkAlt = Color(0xFF3A3A3C)
val SeparatorDark = Color(0xFF38383A)
val TextPrimaryDark = Color(0xFFF5F5F7)
val TextSecondaryDark = Color(0xFF98989D)
val TextTertiaryDark = Color(0xFF636366)

private val LightScheme = lightColorScheme(
    primary = AppleBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBFF),
    onPrimaryContainer = Color(0xFF003A75),
    secondary = AppleIndigo,
    onSecondary = Color.White,
    tertiary = AppleTeal,
    onTertiary = Color.White,
    background = WindowLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SidebarLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SeparatorLight,
    outlineVariant = Color(0xFFE5E5EA),
    error = AppleRed,
    onError = Color.White,
)

private val DarkScheme = darkColorScheme(
    primary = AppleBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0A3A6B),
    onPrimaryContainer = Color(0xFFB8D8FF),
    secondary = Color(0xFF7A78E8),
    onSecondary = Color.White,
    tertiary = Color(0xFF4AD0DE),
    onTertiary = Color(0xFF00201F),
    background = WindowDark,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDarkAlt,
    onSurfaceVariant = TextSecondaryDark,
    outline = SeparatorDark,
    outlineVariant = SeparatorDark,
    error = Color(0xFFFF453A),
    onError = Color.White,
)

/*
 * San Francisco tidak boleh dibundel (lisensi platform Apple).
 * Font sistem Android (Roboto) adalah pengganti terdekat secara metrik.
 * Skala ukuran mengikuti HIG: 11 / 13 / 15 / 17 / 20 / 22 / 28 / 34.
 */
private val AppleType = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.8).sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        letterSpacing = (-0.3).sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = (-0.2).sp,
        lineHeight = 21.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = (-0.2).sp,
        lineHeight = 20.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = (-0.1).sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = (-0.1).sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
    ),
)

@Composable
fun PixGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppleType,
        content = content,
    )
}