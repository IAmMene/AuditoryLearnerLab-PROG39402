package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import week11.st465546.auditorylearnerlab.components.DetailedQuizProgressBar
import week11.st465546.auditorylearnerlab.components.QuizProgressBar
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel
import kotlin.math.roundToInt

/**
 * The core gameplay screen where users take the quiz.
 *
 * Integrated Features:
 * 1. **Text-To-Speech (TTS):** Reads questions and feedback aloud via TTSManager.
 * 2. **Speech-To-Text (STT):** Captures user voice answers via SpeechRecognitionManager.
 * 3. **Permission Handling:** Manages RECORD_AUDIO permission at runtime.
 */
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onCreateQuiz: () -> Unit,
    onTakeQuiz: (String) -> Unit,
    onEditQuiz: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val quizzes by viewModel.quizzes.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Button(
            onClick = onCreateQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Study Set")
        }

        Spacer(Modifier.height(16.dp))

        Text("Your Study Sets", style = MaterialTheme.typography.headlineSmall)

        if (quizzes.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "No quizzes yet. Create your first study set!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        LazyColumn {
            items(quizzes) { quiz ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    // Content Wrapper with Padding
                    Column(Modifier.padding(12.dp)) {

                        // Title and Question Count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(quiz.title, fontWeight = FontWeight.Bold)
                            Text(
                                "${quiz.questions.size} questions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            //Score Badge
                            quiz.latestScore?.let { score ->
                                Badge(
                                    containerColor = if (score >= 0.7) MaterialTheme.colorScheme.primaryContainer
                                    else if (score >= 0.5) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.errorContainer,
                                    contentColor = if (score >= 0.7) MaterialTheme.colorScheme.primary
                                    else if (score >= 0.5) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.error
                                ) {
                                    Text("${(score * 100).roundToInt()}%")
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Row for "Take Quiz" and "Edit" buttons
                        //Progress Bar Showing Overall Performance
                        // Progress bar showing overall performance
                        if (quiz.latestScore != null) {
                            // Calculate correct answers from latest score
                            val totalQuestions = quiz.questions.size
                            val correctAnswers = (quiz.latestScore!! * totalQuestions).toInt()

                            DetailedQuizProgressBar(
                                correctAnswers = correctAnswers,
                                totalQuestions = totalQuestions,
                                bestScore = quiz.bestScore,
                                latestScore = quiz.latestScore,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(12.dp))
                        } else {
                            // No score yet - show empty progress bar
                            QuizProgressBar(
                                correctAnswers = 0,
                                totalQuestions = quiz.questions.size,
                                modifier = Modifier.fillMaxWidth(),
                                showText = true
                            )

                            Spacer(Modifier.height(12.dp))
                        }


                        // The Take Quiz Button is in its own Row
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Take Quiz Button
                            Button(
                                onClick = { onTakeQuiz(quiz.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Take Quiz")
                            }

                            Spacer(Modifier.width(8.dp))

                            // Edit Button
                            Button(
                                onClick = { onEditQuiz(quiz.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Edit")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Delete Button
                        Button(
                            onClick = { viewModel.deleteQuiz(quiz.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}