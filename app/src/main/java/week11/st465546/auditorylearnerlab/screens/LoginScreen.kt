package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.auth.ViewModel
import week11.st465546.auditorylearnerlab.nav.Routes
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField

@Composable
fun LoginScreen(vm: ViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        AppTextField(
            label = "Email",
            value = state.email,
            onValueChange = vm::updateEmail
        )

        AppTextField(
            label = "Password",
            value = state.password,
            onValueChange = vm::updatePassword,
            isPassword = true
        )

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        AppButton(
            text = if (state.loading) "Loading…" else "Login",
            enabled = !state.loading
        ) {
            vm.login {
                nav.navigate(Routes.HOME) {
                    popUpTo(0)
                }
            }
        }

        TextButton(onClick = { nav.navigate(Routes.REGISTER) }) {
            Text("Create Account")
        }

        TextButton(onClick = { nav.navigate(Routes.FORGOT) }) {
            Text("Forgot Password?")
        }
    }
}