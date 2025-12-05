package week11.st465546.auditorylearnerlab.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import week11.st465546.auditorylearnerlab.R
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.nav.Routes
import week11.st465546.auditorylearnerlab.components.AppButton
import week11.st465546.auditorylearnerlab.components.AppTextField
import week11.st465546.auditorylearnerlab.ui.theme.DarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: AuthViewModel, nav: NavController) {
    val state = vm.state.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon and Welcome in the same Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.logo), // Your PNG
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(56.dp)
            )

            Spacer(Modifier.width(16.dp)) // Space between icon and text

            // Welcome text
            Text(
                "Welcome",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = DarkGreen,
                    fontSize = 36.sp
                )
            )
        }

        Spacer(Modifier.height(40.dp))

        //Reusable Component
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
            Text(it, color = MaterialTheme.colorScheme.error,  modifier = Modifier.padding(top = 8.dp))
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

        // Centered column for the two text buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Create Account button in DarkGreen
            TextButton(
                onClick = { nav.navigate(Routes.REGISTER) }
            ) {
                Text(
                    "Create Account",
                    color = DarkGreen  // Use DarkGreen color
                )
            }

            // Forgot Password button in DarkGreen
            TextButton(
                onClick = { nav.navigate(Routes.FORGOT) }
            ) {
                Text(
                    "Forgot Password?",
                    color = DarkGreen  // Use DarkGreen color
                )
            }
        }
    }
}