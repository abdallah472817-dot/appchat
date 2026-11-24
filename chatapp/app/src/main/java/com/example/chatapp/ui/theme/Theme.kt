package com.example.chatapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Neo-Brutalist palette.
private val Cream = Color(0xFFF6F2E9)
private val PaleRose = Color(0xFFF9E3DE)
private val White = Color(0xFFFFFFFF)
private val Ink = Color(0xFF111111)
private val AccentRed = Color(0xFFD62828)

private val Charcoal = Color(0xFF161616)
private val DarkCard = Color(0xFF222222)
private val AccentRedDark = Color(0xFFE63946)

/** Shared design tokens: hard offset shadows and almost-sharp corners. */
val NeoBrutalistCornerShape = RoundedCornerShape(2.dp)
val NeoBrutalistBorderWidth = 2.dp
val NeoBrutalistShadowOffset = 4.dp

val NeoBrutalistLightColorScheme = lightColorScheme(
    primary = AccentRed,
    onPrimary = White,
    primaryContainer = PaleRose,
    onPrimaryContainer = Ink,
    secondary = Ink,
    onSecondary = White,
    background = Cream,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = PaleRose,
    onSurfaceVariant = Ink,
    surfaceTint = Color.Transparent,
    outline = Ink,
    outlineVariant = Ink,
    error = AccentRed,
    onError = White,
    scrim = Ink,
)

val NeoBrutalistDarkColorScheme = darkColorScheme(
    primary = AccentRedDark,
    onPrimary = White,
    primaryContainer = DarkCard,
    onPrimaryContainer = Cream,
    secondary = Cream,
    onSecondary = Charcoal,
    background = Charcoal,
    onBackground = Cream,
    surface = DarkCard,
    onSurface = Cream,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Cream,
    surfaceTint = Color.Transparent,
    outline = Cream,
    outlineVariant = Cream,
    error = AccentRedDark,
    onError = White,
    scrim = Cream,
)

@Composable
fun NeoBrutalistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) NeoBrutalistDarkColorScheme else NeoBrutalistLightColorScheme,
        typography = NeoBrutalistTypography,
        content = content,
    )
}
