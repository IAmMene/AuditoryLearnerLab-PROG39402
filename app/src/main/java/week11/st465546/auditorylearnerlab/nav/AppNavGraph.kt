package week11.st465546.auditorylearnerlab.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import week11.st465546.auditorylearnerlab.auth.ViewModel
import week11.st465546.auditorylearnerlab.screens.ForgotPasswordScreen
import week11.st465546.auditorylearnerlab.screens.LoginScreen
import week11.st465546.auditorylearnerlab.screens.RegisterScreen
import week11.st465546.auditorylearnerlab.screens.HomeScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home"
}

@Composable
fun AppNavGraph(viewModel: ViewModel) {
    val navController = rememberNavController()

    val startDestination = if (viewModel.isUserLoggedIn()) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(viewModel, navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(viewModel, navController)
        }

        composable(Routes.FORGOT) {
            ForgotPasswordScreen(viewModel, navController)
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}