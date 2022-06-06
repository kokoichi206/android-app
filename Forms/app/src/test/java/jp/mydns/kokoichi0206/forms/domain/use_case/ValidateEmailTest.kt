package jp.mydns.kokoichi0206.forms.domain.use_case

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ValidateEmailTest {

    private lateinit var validatePassword: ValidatePassword

    @Before
    fun setUp() {
        validatePassword = ValidatePassword()
    }

    @Test
    fun `Password is letter-only, returns error`() {
        val result = validatePassword.execute("abcdfae")

        assertFalse(result.successful)
    }
}