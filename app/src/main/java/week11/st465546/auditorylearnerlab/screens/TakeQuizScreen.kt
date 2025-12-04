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
import androidx.compose.ui.text.font.FontWeight
import week11.st465546.auditorylearnerlab.components.QuizProgressBar
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel
import kotlin.math.roundToInt

@Composable
fun TakeQuizScreen(
    quiz: Quiz,
    onBack: () -> Unit
) {

    // At the top of TakeQuizScreen, you need access to the ViewModel
    val homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Add these variables for progress tracking
    var correctCount by remember { mutableStateOf(0) }
    var answeredQuestions by remember { mutableStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf<Float?>(null) }


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

    //Define the check function to update the UI and add tracck progress
    fun checkAnswer(answerToCheck: String, question: Question) {
        val correctAnswer = question.options.getOrNull(question.correctIndex)
        // Check if the answer matches (ignoring case)
        val isCorrect = answerToCheck.trim().equals(correctAnswer, ignoreCase = true)

        // Update the progress tracking
        if (isCorrect) {
            correctCount++
        }
        answeredQuestions++

        // Update the state -> This triggers the UI to show Green/Red box
        isAnswerCorrect = isCorrect
    }
    // Function to calculate and display final results
    fun showResults() {
        val totalQuestions = quiz.questions.size
        finalScore = if (totalQuestions > 0) {
            correctCount.toFloat() / totalQuestions.toFloat()
        } else 0f
        quizCompleted = true

        // Save the score to Firestore
        homeViewModel.saveQuizAttempt(quiz.id, correctCount, totalQuestions)

        // Speak final results
        coroutineScope.launch {
            val scoreText = if (finalScore!! >= 0.7) "Excellent! "
            else if (finalScore!! >= 0.5) "Good job! "
            else "Keep practicing! "
            val percentage = (finalScore!! * 100).roundToInt()
            ttsManager.speak("Quiz completed! $scoreText You got $correctCount out of $totalQuestions questions correct. That's $percentage percent.")
        }
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

    // If quiz is completed, show results screen
    if (quizCompleted && finalScore != null) {
        QuizResultsScreen(
            quiz = quiz,
            correctCount = correctCount,
            totalQuestions = quiz.questions.size,
            finalScore = finalScore!!,
            onBack = onBack,
            ttsManager = ttsManager
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quiz Title with Progress Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Current score badge
                    if (answeredQuestions > 0) {
                        val currentPercentage = if (quiz.questions.size > 0) {
                            (correctCount.toFloat() / answeredQuestions.toFloat() * 100).roundToInt()
                        } else 0

                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text("$currentPercentage%")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${quiz.questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Score: $correctCount/${quiz.questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar for current quiz
                QuizProgressBar(
                    correctAnswers = correctCount,
                    totalQuestions = quiz.questions.size,
                    currentQuestion = currentQuestionIndex + 1,
                    modifier = Modifier.fillMaxWidth(),
                    showText = true,
                    height = 24
                )
            }
        }

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
                    text = "Question ${currentQuestionIndex + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose the correct answer:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                currentQuestion?.let { question ->
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
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
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Read Question")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

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
                            },
                            modifier = Modifier.weight(1f)
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
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (userAnswer == option) {
                                    when (isAnswerCorrect) {
                                        true -> MaterialTheme.colorScheme.primaryContainer
                                        false -> MaterialTheme.colorScheme.errorContainer
                                        null -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text(
                                text = "${index + 1}. $option",
                                modifier = Modifier.padding(12.dp),
                                color = if (userAnswer == option && isAnswerCorrect != null) {
                                    if (isAnswerCorrect == true) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAnswerCorrect == null // Disable if answer already submitted
                )

                Button(
                    onClick = {
                        currentQuestion?.let { question ->
                            if (isAnswerCorrect == null) { // Only submit if not already answered
                                checkAnswer(userAnswer, question)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = userAnswer.isNotBlank() && isAnswerCorrect == null
                ) {
                    Text("Submit Answer")
                }
            }
        }

        // Feedback Section - Show only if answer has been submitted
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (correct) "Correct! ✓" else "Incorrect ✗",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Mini progress for this question
                        Text(
                            text = "Score: $correctCount/${quiz.questions.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

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
                        // Last question answered, show results
                        showResults()
                    }
                },
                enabled = isAnswerCorrect != null // Only allow next if current question answered
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