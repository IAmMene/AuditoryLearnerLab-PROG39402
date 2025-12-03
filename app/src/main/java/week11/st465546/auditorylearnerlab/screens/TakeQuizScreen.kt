package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import week11.st465546.auditorylearnerlab.model.Question
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.tts.TTSManager
import week11.st465546.auditorylearnerlab.speech.SpeechRecognitionManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest

@Composable
fun TakeQuizScreen(
    quiz: Quiz,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ttsManager = remember { TTSManager(context) }
    val speechManager = remember { SpeechRecognitionManager(context) }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    val speechState by speechManager.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    //Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                speechManager.startListening()
            } else {
                // Handle denial
                println("Permission denied")
            }
        }
    )

    //Define the reset function to clear previous answers
    fun resetAnswerState() {
        isAnswerCorrect = null
        userAnswer = ""
    }

    //Define the check function to update the UI
    fun checkAnswer(answerToCheck: String, question: Question) {
        val correctAnswer = question.options.getOrNull(question.correctIndex)
        // Check if the answer matches (ignoring case)
        val isCorrect = answerToCheck.trim().equals(correctAnswer, ignoreCase = true)

        // Update the state -> This triggers the UI to show Green/Red box
        isAnswerCorrect = isCorrect
    }

    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)

    // Handle speech recognition results
    LaunchedEffect(speechState.recognizedText) {
        if (speechState.recognizedText.isNotEmpty()) {
            userAnswer = speechState.recognizedText
            // Auto-check if it matches an option
            currentQuestion?.let { question ->
                val matchedOption = question.options.find { option ->
                    option.equals(speechState.recognizedText, ignoreCase = true)
                }
                if (matchedOption != null) {
                    checkAnswer(matchedOption, question)
                }
            }
        }
    }

    // Handle speech errors
    LaunchedEffect(speechState.error) {
        speechState.error?.let { error ->
            // You could show a toast or update UI here
            println("Speech error: $error")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quiz Title
        Text(
            text = quiz.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Question Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${quiz.questions.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                currentQuestion?.let { question ->
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // TTS Controls
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    ttsManager.speak("Question: ${question.text}")
                                }
                            }
                        ) {
                            Text("Read Question")
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    // 1. Speak the intro phrase (Flushing any previous audio)
                                    ttsManager.speak("Here are the options:", queueMode = TextToSpeech.QUEUE_FLUSH)

                                    // 2. Loop through options and ADD them to the queue
                                    question.options.forEachIndexed { index, option ->
                                        ttsManager.speak(
                                            text = "Option ${index + 1}: $option",
                                            queueMode = TextToSpeech.QUEUE_ADD // Ensures they play sequentially
                                        )
                                    }
                                }
                            }
                        ) {
                            Text("Read Options")
                        }
                    }

                    // Options
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Options:", style = MaterialTheme.typography.titleSmall)

                    question.options.forEachIndexed { index, option ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                userAnswer = option
                                checkAnswer(option, question)
                            }
                        ) {
                            Text(
                                text = "${index + 1}. $option",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Speech Recognition Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Answer with Voice",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Speech recognition status
                if (speechState.isListening) {
                    Text(
                        text = "Listening... Speak now",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Voice Input Button
                Button(
                    onClick = {
                        if (speechState.isListening) {
                            speechManager.stopListening()
                        } else {
                            // Check if we need to ask for permission first
                            if (speechManager.checkPermission()) {
                                speechManager.startListening()
                            } else {
                                // ASK for permission
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (speechState.isListening) "Stop Listening" else "Start Speaking")
                }

                // Show recognized text
                if (speechState.recognizedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You said: ${speechState.recognizedText}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Text input fallback
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Or type your answer") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        currentQuestion?.let { question ->
                            checkAnswer(userAnswer, question)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = userAnswer.isNotBlank()
                ) {
                    Text("Submit Answer")
                }
            }
        }

        // Feedback Section
        isAnswerCorrect?.let { correct ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (correct) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (correct) "Correct! ✓" else "Incorrect ✗",
                        style = MaterialTheme.typography.titleMedium
                    )

                    currentQuestion?.let { question ->
                        if (!correct) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Correct answer: ${question.options[question.correctIndex]}")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                ttsManager.speak(
                                    if (correct) "Correct! Well done."
                                    else "Incorrect. The correct answer is ${currentQuestion?.options?.get(currentQuestion.correctIndex)}"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hear Feedback")
                    }
                }
            }
        }

        // Navigation Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (currentQuestionIndex > 0) {
                        currentQuestionIndex--
                        resetAnswerState()
                    }
                },
                enabled = currentQuestionIndex > 0
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    if (currentQuestionIndex < quiz.questions.size - 1) {
                        currentQuestionIndex++
                        resetAnswerState()
                    } else {
                        onBack()
                    }
                }
            ) {
                Text(
                    if (currentQuestionIndex < quiz.questions.size - 1) "Next Question"
                    else "Finish Quiz"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("TTS Settings", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                var speechRate by remember { mutableStateOf(0.9f) }
                Text("Speech Speed: ${"%.1f".format(speechRate)}x")
                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        ttsManager.setSpeechRate(it)
                    },
                    valueRange = 0.5f..2.0f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back to Home")
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
            speechManager.destroy()
        }
    }
}
