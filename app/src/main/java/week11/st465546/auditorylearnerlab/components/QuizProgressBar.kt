package week11.st465546.auditorylearnerlab.components

/**
 * QuizProgressBar.kt
 * Code for a reusable component to show the progress of a quiz during the quiz
 *
 * Author: Mariah
 * DATE updated: Dec 4, 2025
 */


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QuizProgressBar(
    correctAnswers: Int,
    totalQuestions: Int,
    currentQuestion: Int? = null, // Add this parameter
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    height: Int = 24
) {
    if (totalQuestions == 0) return

    val progress = if (totalQuestions > 0) {
        correctAnswers.toFloat() / totalQuestions.toFloat()
    } else 0f

    // Calculate current question progress if provided
    val currentProgress = if (currentQuestion != null && totalQuestions > 0) {
        (currentQuestion - 1).toFloat() / totalQuestions.toFloat() // -1 because we want progress up to previous question
    } else null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.CenterStart
    ) {
        // Correct answers (green)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
        )

        // Incorrect answers (gray for unanswered/unattempted)
        if (correctAnswers < totalQuestions) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f - progress)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (currentQuestion == null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    .align(Alignment.CenterEnd)
            )
        }
        // Current question position indicator (if provided)
        currentProgress?.let { cp ->
            if (cp > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .fillMaxWidth(cp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                )
            }
        }
        if (showText) {
            Text(
                text = if (currentQuestion != null)
                    "Q$currentQuestion/$totalQuestions | Score: $correctAnswers/$totalQuestions"
                else "$correctAnswers/$totalQuestions",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}