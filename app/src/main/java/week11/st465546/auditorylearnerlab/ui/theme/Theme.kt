package week11.st465546.auditorylearnerlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Create a light color scheme for white background
private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,

    secondary = GreyBlueSecondary,
    onSecondary = White,

    background = White,  // White background for light mode
    onBackground = DarkGreen,  // Dark green text on white background

    surface = White,
    onSurface = DarkGreen
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = White,

    secondary = GreyBlueSecondary,
    onSecondary = White,

    background = DarkBackground,  // Use DarkBackground instead of DarkGreen
    onBackground = White,

    surface = DarkBackground,
    onSurface = White
)

@Composable
fun AuditoryLearnerLabTheme(
    darkTheme: Boolean = false,  // Add parameter to toggle dark/light
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}