import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel

@Composable
fun CreateQuizScreen(vm: HomeViewModel, nav: NavController) {

    val ui by vm.ui.collectAsState()

    var questionText by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(0) }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = ui.title,
            onValueChange = vm::updateTitle,
            label = { Text("Quiz Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Add a question
        Text("Add Question")
        OutlinedTextField(value = questionText, onValueChange = { questionText = it }, label = { Text("Question") })
        OutlinedTextField(value = option1, onValueChange = { option1 = it }, label = { Text("Option 1") })
        OutlinedTextField(value = option2, onValueChange = { option2 = it }, label = { Text("Option 2") })
        OutlinedTextField(value = option3, onValueChange = { option3 = it }, label = { Text("Option 3") })

        Spacer(Modifier.height(8.dp))

        Text("Correct answer index (0,1,2)")
        OutlinedTextField(
            value = correctIndex.toString(),
            onValueChange = { if (it.toIntOrNull() != null) correctIndex = it.toInt() },
        )

        Button(
            onClick = {
                vm.addQuestion(
                    Question(
                        text = questionText,
                        options = listOf(option1, option2, option3),
                        correctIndex = correctIndex
                    )
                )
                questionText = ""
                option1 = ""
                option2 = ""
                option3 = ""
            }
        ) {
            Text("Add Question")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                vm.saveQuiz {
                    nav.popBackStack()
                }
            },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Study Set")
        }

        ui.error?.let {
            Text(it, color = Color.Red)
        }
    }
}
