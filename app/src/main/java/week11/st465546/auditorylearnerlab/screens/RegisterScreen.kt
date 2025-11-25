package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.nav.Routes
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField

@Composable
fun RegisterScreen(vm: AuthViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        AppTextField("Email", state.email, vm::updateEmail)
        AppTextField("Password", state.password, vm::updatePassword, isPassword = true)

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(12.dp))

        AppButton(
            text = if (state.loading) "Loading…" else "Register",
            enabled = !state.loading
        ) {
            vm.register {
                nav.navigate(Routes.HOME) { popUpTo(0) }
            }
        }
    }
}