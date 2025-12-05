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
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen
import week11.st465546.auditorylearnerlab.ui.theme.GreenPrimary
import week11.st465546.auditorylearnerlab.ui.theme.White
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
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

//Initialize the Speech and TTS Managers
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
    Surface(
        color = DarkGreen,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = White
                    )
                ) {
                    Text("← Back")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Quiz Title - Centered
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = White,
                    fontSize = 32.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Quiz Title with Progress Indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Question Counter
                        Text(
                            text = "Question ${currentQuestionIndex + 1}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = DarkGreen
                            )
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
                    Spacer(modifier = Modifier.height(10.dp))

                    // Score Display
                    Text(
                        text = "Score: $correctCount/${quiz.questions.size}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DarkGreen,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
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
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    //Question Header
                    Text(
                        text = "Question ${currentQuestionIndex + 1}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    currentQuestion?.let { question ->
                        // Question Text
                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = DarkGreen,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        //TTS Controls
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            //Read Question
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        ttsManager.speak("Question: ${question.text}")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = GreenPrimary
                                )
                            ) {
                                Text("Read Question")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            //Read Options
                            // Read Options Button
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        // 1. Speak the intro phrase (Flushing any previous audio)
                                        ttsManager.speak(
                                            "Here are the options:",
                                            queueMode = TextToSpeech.QUEUE_FLUSH
                                        )
                                        // 2. Loop through options and ADD them to the queue
                                        question.options.forEachIndexed { index, option ->
                                            ttsManager.speak(
                                                text = "Option ${index + 1}: $option",
                                                queueMode = TextToSpeech.QUEUE_ADD
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = GreenPrimary
                                )
                            ) {
                                Text("Read Options")
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        //Read Options
                        Text(
                            text = "Choose the correct answer:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GreyBlueSecondary
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        //Options List
                        question.options.forEachIndexed { index, option ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                onClick = {
                                    userAnswer = option
                                    checkAnswer(option, question)
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (userAnswer == option) {
                                        when (isAnswerCorrect) {
                                            true -> GreenPrimary.copy(alpha = 0.2f)
                                            false -> Color.Red.copy(alpha = 0.2f)
                                            null -> GreyBlueSecondary.copy(alpha = 0.1f)
                                        }
                                    } else {
                                        Color.Transparent
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (userAnswer == option) 2.dp else 0.dp
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
            //Answer Submission Card and Speech Recognition Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Submit Your Answer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = DarkGreen
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    //VOice Input
                    Column(
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Answer with Voice",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GreyBlueSecondary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Speech recognition status
                        if (speechState.isListening) {
                            Text(
                                text = "Listening... Speak now",
                                color = GreenPrimary,
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
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (speechState.isListening)
                                    GreenPrimary.copy(alpha = 0.8f)
                                else
                                    GreenPrimary,
                                contentColor = White
                            )
                        ) {
                            Text(if (speechState.isListening) "Stop Listening" else "Start Speaking")
                        }

                        //Show Recognition Text
                        if (speechState.recognizedText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = GreyBlueSecondary.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = "You said: ${speechState.recognizedText}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkGreen,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                    // Text input fallback
                    // Text Input Section
                    Column {
                        Text(
                            text = "Or type your answer:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GreyBlueSecondary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = {
                                Text(
                                    "Or type your answer",
                                    color = GreyBlueSecondary
                                )
                            },
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

                //Feed Back Section
                isAnswerCorrect?.let { correct ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (correct) GreenPrimary.copy(alpha = 0.1f)
                            else Color.Red.copy(0.1f)
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
                                    text = if (correct) "Correct! " else "Incorrect ",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
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
                                            else "Incorrect. The correct answer is ${
                                                currentQuestion?.options?.get(
                                                    currentQuestion.correctIndex
                                                )
                                            }"
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
                    OutlinedButton(
                        onClick = {
                            if (currentQuestionIndex > 0) {
                                currentQuestionIndex--
                                resetAnswerState()
                            }
                        },
                        enabled = currentQuestionIndex > 0,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = White
                        )
                    ) {
                        Text("Previous")
                    }
                    //Next Question
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
                        enabled = isAnswerCorrect != null, // Only allow next if current question answered
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = GreenPrimary
                        )
                    ) {
                        Text(
                            if (currentQuestionIndex < quiz.questions.size - 1) "Next Question"
                            else "Finish Quiz"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // TTS Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("TTS Settings", style = MaterialTheme.typography.titleSmall.copy(color = DarkGreen),
                            modifier = Modifier.padding(bottom = 8.dp))

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
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = GreenPrimary,
                                activeTrackColor = GreenPrimary,
                                inactiveTrackColor = GreyBlueSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }
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
    }
}


