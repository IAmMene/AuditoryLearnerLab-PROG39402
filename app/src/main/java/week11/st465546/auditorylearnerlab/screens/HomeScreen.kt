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
import androidx.compose.ui.unit.sp
import week11.st465546.auditorylearnerlab.components.DetailedQuizProgressBar
import week11.st465546.auditorylearnerlab.components.QuizProgressBar
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen
import week11.st465546.auditorylearnerlab.ui.theme.GreenPrimary
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary
import week11.st465546.auditorylearnerlab.ui.theme.White
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onCreateQuiz: () -> Unit,
    onTakeQuiz: (String) -> Unit,
    onEditQuiz: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val quizzes by viewModel.quizzes.collectAsState()

    // Use Surface to set the dark green background
    Surface(
        color = DarkGreen,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Title in White
            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = White,
                    fontSize = 32.sp // Slightly larger
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Create Study Set Button - White with GreenPrimary text
            OutlinedButton(
                onClick = onCreateQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor = GreenPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp
                )
            ) {
                Text(
                    "Create Study Set",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Your Study Sets Section
            Text(
                "Your Study Sets",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (quizzes.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No study sets yet.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = White
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Create your first study set to get started!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White.copy(alpha = 0.8f)
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quizzes) { quiz ->
                    // Quiz Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Title and Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    quiz.title,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = DarkGreen,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                // Question Count
                                Text(
                                    "${quiz.questions.size} questions",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GreyBlueSecondary
                                    )
                                )

                                // Score Badge
                                quiz.latestScore?.let { score ->
                                    Badge(
                                        containerColor = when {
                                            score >= 0.7 -> Color(0xFF4CAF50) // Green
                                            score >= 0.5 -> Color(0xFFFF9800) // Orange
                                            else -> Color(0xFFF44336) // Red
                                        },
                                        contentColor = White
                                    ) {
                                        Text("${(score * 100).roundToInt()}%")
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Progress Bar
                            if (quiz.latestScore != null) {
                                val totalQuestions = quiz.questions.size
                                val correctAnswers = (quiz.latestScore!! * totalQuestions).toInt()

                                DetailedQuizProgressBar(
                                    correctAnswers = correctAnswers,
                                    totalQuestions = totalQuestions,
                                    bestScore = quiz.bestScore,
                                    latestScore = quiz.latestScore,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                // No score yet
                                QuizProgressBar(
                                    correctAnswers = 0,
                                    totalQuestions = quiz.questions.size,
                                    modifier = Modifier.fillMaxWidth(),
                                    showText = true
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Action Buttons Row - Edit, Delete, Take Quiz
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Edit Button
                                OutlinedButton(
                                    onClick = { onEditQuiz(quiz.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = GreenPrimary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 1.dp
                                    )
                                ) {
                                    Text("Edit")
                                }

                                Spacer(Modifier.width(8.dp))

                                // Delete Button
                                OutlinedButton(
                                    onClick = { viewModel.deleteQuiz(quiz.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.Red
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 1.dp
                                    )
                                ) {
                                    Text("Delete")
                                }

                                Spacer(Modifier.width(8.dp))

                                // Take Quiz Button
                                Button(
                                    onClick = { onTakeQuiz(quiz.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenPrimary,
                                        contentColor = White
                                    )
                                ) {
                                    Text("Take")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = White
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                Text("Logout")
            }
        }
    }
}