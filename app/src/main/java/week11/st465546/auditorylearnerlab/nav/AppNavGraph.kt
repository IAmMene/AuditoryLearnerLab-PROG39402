package week11.st465546.auditorylearnerlab.nav

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

object Routes {
    const val LOGIN = "login" //login page
    const val REGISTER = "register" //sign up page
    const val FORGOT = "forgot" //forgot password page
    const val HOME = "home" //home page
}

@Composable
fun AppNavGraph(vm: AuthViewModel) {
    val navController = rememberNavController()

    val startDestination = if (vm.isUserLoggedIn()) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(vm, navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(vm, navController)
        }

        composable(Routes.FORGOT) {
            ForgotPasswordScreen(vm, navController)
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    vm.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}