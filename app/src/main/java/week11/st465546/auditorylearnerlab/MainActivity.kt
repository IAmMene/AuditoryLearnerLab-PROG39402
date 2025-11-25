package week11.st465546.auditorylearnerlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                //changed the viewmodel to be activity scoped -Mariah
                val vm: AuthViewModel = viewModel()
                val quizVm: HomeViewModel = viewModel()
                AppNavGraph(vm,quizVm)
            }
        }
    }
}