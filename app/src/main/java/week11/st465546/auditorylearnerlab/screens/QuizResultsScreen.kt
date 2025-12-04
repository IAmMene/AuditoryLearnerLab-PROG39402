package week11.st465546.auditorylearnerlab.screens

/**
 * Quiz Results Screen after the QUiz is completed to show the progress
 */
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.tts.TTSManager
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.drawscope.Stroke
import week11.st465546.auditorylearnerlab.components.ScoreCircle
import kotlin.math.roundToInt

@Composable
fun QuizResultsScreen(
    quiz: Quiz,
    correctCount: Int,
    totalQuestions: Int,
    finalScore: Float,
    onBack: () -> Unit,
    ttsManager: TTSManager
) {
    val percentage = (finalScore * 100).roundToInt()
    val performanceMessage = when {
        finalScore >= 0.9 -> "Outstanding! 🎉"
        finalScore >= 0.7 -> "Great job! 👍"
        finalScore >= 0.5 -> "Good effort! 😊"
        else -> "Keep practicing! 📚"
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Results card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    finalScore >= 0.7 -> MaterialTheme.colorScheme.primaryContainer
                    finalScore >= 0.5 -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quiz Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScoreCircle(
                    finalScore = finalScore,
                    percentage = percentage,
                    correctCount = correctCount,
                    totalQuestions = totalQuestions,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = performanceMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed breakdown
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quiz: ${quiz.title}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Questions: $totalQuestions",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Correct Answers: $correctCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Incorrect Answers: ${totalQuestions - correctCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back to Home")
                    }
                }
            }
        }
    }
}