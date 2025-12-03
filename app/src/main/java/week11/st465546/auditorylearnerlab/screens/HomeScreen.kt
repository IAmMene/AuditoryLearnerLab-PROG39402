package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onCreateQuiz: () -> Unit,
    onTakeQuiz: (String) -> Unit,
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


        LazyColumn {
            items(quizzes) { quiz ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    // Use a Row to arrange text and button horizontally
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, // Pushes elements to edges
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        // Column for Title and Question count
                        Column(modifier = Modifier.weight(1f)) {
                            Text(quiz.title, fontWeight = FontWeight.Bold)
                            Text("${quiz.questions.size} questions")
                        }

                        // The Delete Button
                        IconButton(onClick = { viewModel.deleteQuiz(quiz.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Quiz",
                                tint = MaterialTheme.colorScheme.error // Makes icon red
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onTakeQuiz(quiz.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Take Quiz")
                            }

                            Spacer(Modifier.width(8.dp))

//                            Button(
//                                onClick = { onViewFlashCards(quiz.id) },
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text("Flash Cards")
//                            }
                        }

                        Spacer(Modifier.height(8.dp))

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
