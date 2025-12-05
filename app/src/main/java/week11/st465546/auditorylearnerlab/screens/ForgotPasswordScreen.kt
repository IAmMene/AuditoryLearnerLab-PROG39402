package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField
import week11.st465546.auditorylearnerlab.nav.Routes
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen

@Composable
fun ForgotPasswordScreen(vm: AuthViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Reset Password", style = MaterialTheme.typography.headlineLarge.copy(
            color = DarkGreen,
            fontSize = 36.sp
        ))

        Spacer(Modifier.height(40.dp))

        AppTextField("Email", state.email, vm::updateEmail)

        // Error message
        state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }


        AppButton(
            text = if (state.loading) "Sending…" else "Send Reset Email",
            enabled = !state.loading
        ) {
            vm.resetPassword {
                nav.popBackStack()
            }
        }
        Spacer(Modifier.height(16.dp))

        // Already have an account link
        TextButton(
            onClick = {
                nav.navigate(Routes.LOGIN) {
                    // Optional: Clear back stack or keep it
                    // popUpTo(Routes.REGISTER) { inclusive = true }
                }
            }
        ) {
            Text(
                "Remember your password? Sign In",
                color = DarkGreen
            )
        }

    }
}