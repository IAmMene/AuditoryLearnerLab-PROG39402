package week11.st465546.auditorylearnerlab.components

/**
 * ScoreCircle.kt
 * Code for a reusable component to show the score of a quiz
 *
 * Author: Mariah
 * DATE updated: Dec 4, 2025
 */
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScoreCircle(
    finalScore: Float,
    percentage: Int,
    correctCount: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = when {
        finalScore >= 0.7 -> MaterialTheme.colorScheme.primary
        finalScore >= 0.5 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension / 2
            )

            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = finalScore * 360f,
                useCenter = true,
                size = size
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$correctCount/$totalQuestions",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}