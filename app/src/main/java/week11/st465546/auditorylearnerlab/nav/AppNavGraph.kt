package week11.st465546.auditorylearnerlab.nav

import CreateQuizScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.screens.ForgotPasswordScreen
import week11.st465546.auditorylearnerlab.screens.LoginScreen
import week11.st465546.auditorylearnerlab.screens.RegisterScreen
import week11.st465546.auditorylearnerlab.screens.HomeScreen
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel

object Routes {
    const val LOGIN = "login" //login page
    const val REGISTER = "register" //sign up page
    const val FORGOT = "forgot" //forgot password page
    const val HOME = "home" //home page

    const val CREATE_QUIZ = "create_quiz"
    //const val QUIZ_LIST = "quiz_list"
   // const val QUIZ_DETAIL = "quiz_detail"
}

@Composable
fun AppNavGraph(vmAuth: AuthViewModel, vmHome: HomeViewModel) {
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
            HomeScreen(
                onLogout = {
                    vmAuth.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                },   onCreateQuiz = {
                    navController.navigate(Routes.CREATE_QUIZ)
                },
                viewModel = vmHome
            )
        }
        composable(Routes.CREATE_QUIZ) {
            CreateQuizScreen(vmHome, navController)
        }
    }
}