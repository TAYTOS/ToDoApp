package com.example.todoapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Custom Palette ─────────────────────────────────────────────────────────────
val BgDark      = Color(0xFF0E1117)
val CardBg      = Color(0xFF181D27)
val AccentGreen = Color(0xFF00E5A0)
val AccentBlue  = Color(0xFF4FC3F7)
val AccentRed   = Color(0xFFFF4F6A)
val TextMain    = Color(0xFFECEFF4)
val TextMuted   = Color(0xFF6B7490)

private val DarkColors = darkColorScheme(
    primary          = AccentGreen,
    onPrimary        = BgDark,
    secondary        = AccentBlue,
    onSecondary      = BgDark,
    background       = BgDark,
    onBackground     = TextMain,
    surface          = CardBg,
    onSurface        = TextMain,
    surfaceVariant   = Color(0xFF1F2535),
    onSurfaceVariant = TextMuted,
    error            = AccentRed,
    onError          = TextMain
)

@Composable
fun ToDoAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content     = content
    )
}
