package week11.st465546.auditorylearnerlab.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st465546.auditorylearnerlab.ui.theme.GreenPrimary
import week11.st465546.auditorylearnerlab.ui.theme.GreyBlueSecondary

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = GreyBlueSecondary) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            unfocusedBorderColor = GreyBlueSecondary,
            cursorColor = GreenPrimary,
            focusedLabelColor = GreenPrimary,
            unfocusedLabelColor = GreyBlueSecondary
        ),
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium
    )
    Spacer(Modifier.height(12.dp))
}