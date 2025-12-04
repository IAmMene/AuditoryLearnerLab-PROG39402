import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel

/**
 * A shared Composable screen used for both Creating a new quiz and editing an existing one.
 *
 * It observes the HomeViewModel.ui state to populate fields. If the state contains
 * an ID, the Save button functions as an Update button.
 *
 */
@Composable
fun CreateQuizScreen(vm: HomeViewModel, nav: NavController) {

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        val ui by vm.ui.collectAsState()

        var questionText by remember { mutableStateOf("") }
        var option1 by remember { mutableStateOf("") }
        var option2 by remember { mutableStateOf("") }
        var option3 by remember { mutableStateOf("") }
        var correctIndex by remember { mutableStateOf(0) }

        // Add verticalScroll so we can scroll through the list of questions
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = ui.title,
                onValueChange = vm::updateTitle,
                label = { Text("Quiz Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Add New Question", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option1,
                onValueChange = { option1 = it },
                label = { Text("Option 1") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option2,
                onValueChange = { option2 = it },
                label = { Text("Option 2") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = option3,
                onValueChange = { option3 = it },
                label = { Text("Option 3") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Correct answer index (0, 1, or 2)")
            OutlinedTextField(
                value = correctIndex.toString(),
                onValueChange = { if (it.toIntOrNull() != null) correctIndex = it.toInt() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    vm.addQuestion(
                        Question(
                            text = questionText,
                            options = listOf(option1, option2, option3),
                            correctIndex = correctIndex
                        )
                    )
                    // Clear fields after adding
                    questionText = ""
                    option1 = ""
                    option2 = ""
                    option3 = ""
                    correctIndex = 0
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Question")
            }

            Spacer(Modifier.height(24.dp))

            // Display the list of added questions with Delete buttons
            if (ui.questions.isNotEmpty()) {
                Text("Current Questions:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                ui.questions.forEachIndexed { index, question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${index + 1}. ${question.text}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = "Answer: ${question.options.getOrElse(question.correctIndex) { "" }}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // The Delete Button for this specific question
                            IconButton(onClick = { vm.removeQuestion(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Question",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    vm.saveQuiz {
                        nav.popBackStack()
                    }
                },
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.id.isNotEmpty()) "Update Study Set" else "Save Study Set")
            }

            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}