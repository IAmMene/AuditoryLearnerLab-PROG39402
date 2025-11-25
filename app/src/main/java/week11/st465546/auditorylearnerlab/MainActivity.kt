package week11.st465546.auditorylearnerlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import week11.st465546.auditorylearnerlab.auth.AuthViewModel
import week11.st465546.auditorylearnerlab.nav.AppNavGraph
import week11.st465546.auditorylearnerlab.studyset.HomeViewModel
import week11.st465546.auditorylearnerlab.ui.theme.AuditoryLearnerLabTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AuditoryLearnerLabTheme {
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: AuthViewModel = viewModel()
                    val quizVm: HomeViewModel = viewModel()
                    AppNavGraph(vm, quizVm)
                }
            }
        }
    }
}