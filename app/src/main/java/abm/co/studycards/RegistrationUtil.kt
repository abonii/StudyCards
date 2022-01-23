package abm.co.studycards

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS

object RegistrationUtil {

    /**
     * the input is not valid if ...
     * ...the email or password is empty +
     * ...the password&confirmPassword are not same +
     * ...the email is not real email format +
     * ...the password contains less than 6 words +
     */

    fun validateRegistrationInput(
        email: String,
        password: String,
        confirmedPassword: String
    ):Boolean{
        if(email.isEmpty() || password.isEmpty())
            return false
        if(password != confirmedPassword)
            return false
        if(!EMAIL_ADDRESS.matcher(email).matches())
            return false
        if(password.length < 6)
            return false
        return true
    }
}