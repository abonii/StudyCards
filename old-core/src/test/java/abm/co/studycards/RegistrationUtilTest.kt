package abm.co.studycards

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun `empty email returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "123456",
            "123456"
        )
        assertThat(result).isFalse()
    }
    @Test
    fun `username format is not correct returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "abo12",
            "123456",
            "123456"
        )
        assertThat(result).isFalse()
    }
    @Test
    fun `valid email and correctly repeated password returns true`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "abo@mail.ru",
            "123456",
            "123456"
        )
        assertThat(result).isTrue()
    }
    @Test
    fun `the count of password less than 6 returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "abo@mail.ru",
            "12345",
            "12345"
        )
        assertThat(result).isFalse()
    }
    @Test
    fun `empty password returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "abo@mail.ru",
            "",
            ""
        )
        assertThat(result).isFalse()
    }
}