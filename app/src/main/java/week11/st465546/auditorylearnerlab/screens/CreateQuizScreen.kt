package week11.st465546.auditorylearnerlab.screens


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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen
import week11.st465546.auditorylearnerlab.ui.theme.GreenPrimary
import week11.st465546.auditorylearnerlab.ui.theme.White
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary

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
        color = DarkGreen,
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title - Centered
            Text(
                text = if (ui.id.isNotEmpty()) "Edit Study Set" else "Create Study Set",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = White,
                    fontSize = 32.sp
                ),
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            // Study Set Title Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Study Set Title",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = DarkGreen
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = ui.title,
                        onValueChange = vm::updateTitle,
                        label = { Text("Enter Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(20.dp))

                //Question Card Section

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White)
                )
                {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Add New Question",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = DarkGreen
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        //Question
                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            label = { Text("Question") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        //Options

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
                        //Correct Answer
                        Text(
                            "Correct Answer Index (0, 1, or 2)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkGreen
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(0, 1, 2).forEach { index ->
                                FilterChip(
                                    selected = correctIndex == index,
                                    onClick = { correctIndex = index },
                                    label = { Text(index.toString()) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = White,
                                        containerColor = GreyBlueSecondary.copy(alpha = 0.1f),
                                        labelColor = DarkGreen
                                    )
                                )
                            }
                            Spacer(Modifier.height(16.dp))

                            //Add Question Button
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
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary,
                                    contentColor = White
                                )
                            ) {
                                Text("Add Question")
                            }

                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Current Questions (${ui.questions.size})",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = DarkGreen
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        ui.questions.forEachIndexed { index, question ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = GreyBlueSecondary.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${index + 1}. ${question.text}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = DarkGreen,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = "Correct Answer: ${
                                                question.options.getOrElse(
                                                    question.correctIndex
                                                ) { "" }
                                            }",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GreenPrimary
                                            )
                                        )
                                        Text(
                                            text = "Options: ${question.options.joinToString(", ")}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GreyBlueSecondary
                                            )
                                        )
                                    }

                                    // Delete Button
                                    IconButton(
                                        onClick = { vm.removeQuestion(index) },
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
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
                }

                Spacer(Modifier.height(24.dp))
            }

            // Save/Update Button
            Button(
                onClick = {
                    vm.saveQuiz {
                        nav.popBackStack()
                    }
                },
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = GreenPrimary
                )
            ) {
                Text(
                    if (ui.id.isNotEmpty()) "Update Study Set" else "Save Study Set",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Error Message
            ui.error?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

        }
    }
}
