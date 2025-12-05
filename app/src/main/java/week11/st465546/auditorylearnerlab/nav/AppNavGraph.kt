package week11.st465546.auditorylearnerlab.nav


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.model.Quiz
import week11.st465546.auditorylearnerlab.screens.CreateQuizScreen
import week11.st465546.auditorylearnerlab.screens.ForgotPasswordScreen
import week11.st465546.auditorylearnerlab.screens.LoginScreen
import week11.st465546.auditorylearnerlab.screens.RegisterScreen
import week11.st465546.auditorylearnerlab.screens.HomeScreen
import week11.st465546.auditorylearnerlab.screens.TakeQuizScreen
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel


/**
 * Defines the central Navigation Graph for the application using Jetpack Compose Navigation.
 *
 * This composable acts as the router, defining all available screens Routes and
 * managing the arguments passed between them (e.g., passing quizId).
 */
object Routes {
    const val LOGIN = "login" //login page
    const val REGISTER = "register" //sign up page
    const val FORGOT = "forgot" //forgot password page
    const val HOME = "home" //home page
    const val CREATE_QUIZ = "create_quiz"
    const val TAKE_QUIZ = "take_quiz/{quizId}"
    const val EDIT_QUIZ = "edit_quiz/{quizId}" // Added for edit quiz button
}
@Composable
fun AppNavGraph(vmAuth: AuthViewModel) {
    val navController = rememberNavController()

    // Scoped to the NavGraph to share data between Home, Create, and Edit screens.
    // This ensures data persists when navigating between the list and the editor.
    val vmHome: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Determine entry point based on auth state
    val startDestination = if (vmAuth.isUserLoggedIn()) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(vmAuth, navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(vmAuth, navController)
        }
        composable(Routes.FORGOT) {
            ForgotPasswordScreen(vmAuth, navController)
        }
        // Main Dashboard
        composable(Routes.HOME) {
            LaunchedEffect(Unit) {
                vmHome.fetchQuizzes()
            }
            HomeScreen(
                onLogout = {
                    vmAuth.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                },   onCreateQuiz = {
                    vmHome.resetUiState() //Clear form for a new quiz
                    navController.navigate(Routes.CREATE_QUIZ)
                },
                onTakeQuiz = { quizId ->
                    navController.navigate("take_quiz/$quizId")
                },
                //Add the callback for the edit button
                onEditQuiz = { quizId ->
                    navController.navigate("edit_quiz/$quizId")
                },
                viewModel = vmHome
            )
        }
        composable(Routes.CREATE_QUIZ) {
            CreateQuizScreen(vmHome, navController)
        }
        composable(
            route = Routes.EDIT_QUIZ,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            // This block finds the quiz in the list and loads it into the UI
            LaunchedEffect(quizId) {
                val quiz = vmHome.quizzes.value.find { it.id == quizId }
                if (quiz != null) {
                    vmHome.loadQuizForEdit(quiz)
                }
            }
            CreateQuizScreen(vmHome, navController)
        }
        composable(
            route = Routes.TAKE_QUIZ,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val quizzes by vmHome.quizzes.collectAsState()
            val quiz = quizzes.find { it.id == quizId }

            if (quiz != null) {
                TakeQuizScreen(
                    quiz = quiz,
                    onBack = { navController.popBackStack() }
                )
            } else {
                Column {
                    Text("Quiz not found!")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}