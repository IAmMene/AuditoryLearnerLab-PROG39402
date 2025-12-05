package week11.st465546.auditorylearnerlab.components

/**
 * DetailedQuizProgressBar.kt
 * Code for a reusable component to show the progress of a quiz on the home page
 *
 * Author: Mariah
 * DATE updated: Dec 4, 2025
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary


@Composable
fun DetailedQuizProgressBar(
    correctAnswers: Int,
    totalQuestions: Int,
    bestScore: Float? = null,
    latestScore: Float? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Take the Quiz Progress Bar
        QuizProgressBar(
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            modifier = Modifier.fillMaxWidth(),
            showText = true,
            height = 20
        )

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Latest score
            latestScore?.let { score ->
                Text(
                    text = "Latest: ${(score * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = GreyBlueSecondary
                )
            } ?: run {
                //if no latest score show empty text
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall)
            }
            // Best score
            bestScore?.let { score ->
                Text(
                    text = "Best: ${(score * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = GreyBlueSecondary
                )
            }
        }
    }
}