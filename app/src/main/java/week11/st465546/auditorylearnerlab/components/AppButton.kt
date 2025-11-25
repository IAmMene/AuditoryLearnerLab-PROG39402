package week11.st465546.auditorylearnerlab.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st465546.auditorylearnerlab.ui.theme.GreenPrimary
import week11.st465546.auditorylearnerlab.ui.theme.White

@Composable
fun AppButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenPrimary,
            contentColor = White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
    Spacer(Modifier.height(12.dp))
}