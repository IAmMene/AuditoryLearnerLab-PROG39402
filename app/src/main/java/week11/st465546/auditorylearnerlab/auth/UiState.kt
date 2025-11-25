package week11.st465546.auditorylearnerlab.auth

//Data Class for Login/SignUp to display correctly
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)