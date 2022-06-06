package jp.mydns.kokoichi0206.forms.domain.use_case

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null,
)
