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
import week11.st465546.auditorylearnerlab.nav.Routes
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary

@Composable
fun RegisterScreen(vm: AuthViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Create Account", style = MaterialTheme.typography.headlineLarge.copy(
            color = DarkGreen,
            fontSize = 36.sp
        ))

        // Subtitle
        Text(
            "Join Auditory Learner Lab",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = GreyBlueSecondary,
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(40.dp))

        AppTextField("Email", state.email, vm::updateEmail)
        Spacer(Modifier.height(16.dp))
        AppTextField("Password", state.password, vm::updatePassword, isPassword = true)

        // Error message
        state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        AppButton(
            text = if (state.loading) "Loading…" else "Register",
            enabled = !state.loading
        ) {
            vm.register {
                nav.navigate(Routes.HOME) { popUpTo(0) }
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
                "Already have an account? Sign in",
                color = DarkGreen
            )
        }
    }
}