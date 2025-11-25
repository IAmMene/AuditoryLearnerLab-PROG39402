package week11.st465546.auditorylearnerlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = White,

    secondary = GreyBlueSecondary,
    onSecondary = White,

    background = DarkGreen,
    onBackground = White,

    surface = DarkGreen,
    onSurface = White
)

@Composable
fun AuditoryLearnerLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}