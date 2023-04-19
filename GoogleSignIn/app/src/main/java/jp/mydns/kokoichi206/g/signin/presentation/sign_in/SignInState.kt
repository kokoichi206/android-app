package jp.mydns.kokoichi206.g.signin.presentation.sign_in

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)
