package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkSage,
    onPrimary = DarkCreamBase,
    primaryContainer = SageDark,
    onPrimaryContainer = SageLight,
    secondary = DarkLavender,
    onSecondary = DarkCreamBase,
    secondaryContainer = LavenderPrimary,
    onSecondaryContainer = LavenderLight,
    tertiary = DarkPeach,
    onTertiary = DarkCreamBase,
    tertiaryContainer = PeachAccent,
    background = DarkCreamBase,
    onBackground = DarkTextPrimary,
    surface = DarkCreamSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCreamVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = BorderMedium
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SagePrimary,
    onPrimary = CreamSurface,
    primaryContainer = SageContainer,
    onPrimaryContainer = SageDark,
    secondary = LavenderPrimary,
    onSecondary = CreamSurface,
    secondaryContainer = LavenderContainer,
    onSecondaryContainer = LavenderPrimary,
    tertiary = PeachAccent,
    onTertiary = CreamSurface,
    tertiaryContainer = PeachLight,
    background = CreamBase,
    onBackground = TextPrimary,
    surface = CreamSurface,
    onSurface = TextPrimary,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderMedium
  )

@Composable
fun MindNestTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
      }
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MindNestTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
