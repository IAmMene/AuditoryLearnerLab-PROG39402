package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.auth.ViewModel
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField

@Composable
fun ForgotPasswordScreen(vm: ViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        AppTextField("Email", state.email, vm::updateEmail)

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        AppButton(
            text = if (state.loading) "Sending…" else "Send Reset Email",
            enabled = !state.loading
        ) {
            vm.resetPassword {
                nav.popBackStack()
            }
        }
    }
}