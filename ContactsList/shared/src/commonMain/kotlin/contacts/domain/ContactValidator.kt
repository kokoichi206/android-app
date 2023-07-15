package contacts.domain

object ContactValidator {

    fun validateContact(contact: Contact): ValidationResult {
        var result = ValidationResult()

        if (contact.firstName.isBlank()) {
            result = result.copy(firstNameError = "The first name is empty.")
        }

        if (contact.lastName.isBlank()) {
            result = result.copy(lastNameError = "The last name is empty.")
        }

        if (contact.email.isBlank()) {
            result = result.copy(emailError = "The email is empty.")
        }

        val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

        if (!emailRegex.matches(contact.email)) {
            result = result.copy(emailError = "This email is not a valid format.")
        }

        if (contact.phoneNumber.isBlank()) {
            result = result.copy(phoneNumberError = "The phone number is empty.")
        }

        return result
    }

    data class ValidationResult(
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val emailError: String? = null,
        val phoneNumberError: String? = null,
    )
}