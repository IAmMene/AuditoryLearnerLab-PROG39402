package week11.st465546.auditorylearnerlab.nav

import CreateQuizScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import week11.st465546.auditorylearnerlab.screens.ForgotPasswordScreen
import week11.st465546.auditorylearnerlab.screens.LoginScreen
import week11.st465546.auditorylearnerlab.screens.RegisterScreen
import week11.st465546.auditorylearnerlab.screens.HomeScreen
import week11.st465546.auditorylearnerlab.screens.TakeQuizScreen
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel

object Routes {
    const val LOGIN = "login" //login page
    const val REGISTER = "register" //sign up page
    const val FORGOT = "forgot" //forgot password page
    const val HOME = "home" //home page

    const val CREATE_QUIZ = "create_quiz"

    const val TAKE_QUIZ = "take_quiz/{quizId}"
}

@Composable
fun AppNavGraph(vmAuth: AuthViewModel) {
    val navController = rememberNavController()

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

        composable(Routes.HOME) {
            val vmHome: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            HomeScreen(
                onLogout = {
                    vmAuth.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                },   onCreateQuiz = {
                    navController.navigate(Routes.CREATE_QUIZ)
                },
                onTakeQuiz = { quizId ->
                    navController.navigate("take_quiz/$quizId")
                },
                viewModel = vmHome
            )
        }
        composable(Routes.CREATE_QUIZ) {
            val vmHome: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            CreateQuizScreen(vmHome, navController)
        }
        // ADD THESE NEW COMPOSABLES:
        composable(
            route = Routes.TAKE_QUIZ,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""

            // You need to fetch the quiz from your repository
            // For now, let's create a simple placeholder
            val vmHome: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            // Find the quiz with this ID
            val quizzes by vmHome.quizzes.collectAsState()
            val quiz = quizzes.find { it.id == quizId }

            if (quiz != null) {
                TakeQuizScreen(
                    quiz = quiz,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Show error or loading
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