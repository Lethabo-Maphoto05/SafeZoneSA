package com.groupeight.safezonesa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SafeZoneLightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = CardWhite,
    secondary = RoyalBlue,
    onSecondary = CardWhite,
    tertiary = BrightBlue,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    error = AlertRed,
    onError = CardWhite
)

private val SafeZoneDarkColorScheme = darkColorScheme(
    primary = BrightBlue,
    onPrimary = TextPrimary,
    secondary = RoyalBlue,
    onSecondary = CardWhite,
    tertiary = DeepBlue,
    background = Navy,
    onBackground = CardWhite,
    surface = Color(0xFF13223A),
    onSurface = CardWhite,
    error = AlertRedDark,
    onError = CardWhite
)

@Composable
fun SafeZoneSATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SafeZoneDarkColorScheme else SafeZoneLightColorScheme,
        typography = SafeZoneTypography,
        content = content
    )
}