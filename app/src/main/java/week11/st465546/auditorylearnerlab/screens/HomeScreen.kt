package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                    Column(Modifier.padding(12.dp)) {
                        Text(quiz.title, fontWeight = FontWeight.Bold)
                        Text("${quiz.questions.size} questions")
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
