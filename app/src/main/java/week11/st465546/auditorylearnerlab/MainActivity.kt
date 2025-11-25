package week11.st465546.auditorylearnerlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import week11.st465546.auditorylearnerlab.nav.AppNavGraph
import week11.st465546.auditorylearnerlab.auth.ViewModel
import week11.st465546.auditorylearnerlab.ui.theme.AuditoryLearnerLabTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AuditoryLearnerLabTheme {
                AppNavGraph(viewModel = viewModel)
            }
        }
    }
}