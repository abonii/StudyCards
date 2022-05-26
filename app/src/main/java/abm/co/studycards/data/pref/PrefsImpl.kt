package abm.co.studycards.data.pref

import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.Prefs.Companion.ACCESS_TOKEN
import abm.co.studycards.domain.Prefs.Companion.IS_FIRST_TIME
import abm.co.studycards.domain.Prefs.Companion.IS_PREMIUM
import abm.co.studycards.domain.Prefs.Companion.SELECTED_APP_LANGUAGE
import abm.co.studycards.domain.Prefs.Companion.SOURCE_LANGUAGE
import abm.co.studycards.domain.Prefs.Companion.TARGET_LANGUAGE
import abm.co.studycards.domain.Prefs.Companion.USER_ID
import android.content.SharedPreferences
import javax.inject.Inject

class PrefsImpl @Inject constructor(
    private val preferences: SharedPreferences
) : Prefs {

    override fun getAccessToken(): String {
        return preferences.getString(ACCESS_TOKEN, "").orEmpty()
    }

    override fun setAccessToken(value: String) {
        with(preferences.edit()) {
            putString(ACCESS_TOKEN, value)
            commit()
        }
    }

    override fun getUserId(): Int {
        return preferences.getInt(USER_ID, 0)
    }

    override fun setUserId(value: Int) {
        with(preferences.edit()) {
            putInt(USER_ID, value)
            apply()
        }
    }

    override fun removeUserId() {
        with(preferences.edit()) {
            remove(USER_ID)
            apply()
        }
    }

    override fun removeAccessToken() {
        with(preferences.edit()) {
            remove(ACCESS_TOKEN)
            apply()
        }
    }

    override fun getAppLanguage(): String {
        return preferences.getString(SELECTED_APP_LANGUAGE, "") ?: ""
    }

    override fun setAppLanguage(value: String) {
        with(preferences.edit()) {
            putString(SELECTED_APP_LANGUAGE, value)
            commit()
        }
    }

    override fun getSourceLanguage(): String {
        return preferences.getString(SOURCE_LANGUAGE, "") ?: ""
    }

    override fun setSourceLanguage(value: String) {
        with(preferences.edit()) {
            putString(SOURCE_LANGUAGE, value)
            commit()
        }
    }

    override fun getTargetLanguage(): String {
        return preferences.getString(TARGET_LANGUAGE, "") ?: ""
    }

    override fun setTargetLanguage(value: String) {
        with(preferences.edit()) {
            putString(TARGET_LANGUAGE, value)
            commit()
        }
    }

    override fun getIsPremium(): Boolean {
        return preferences.getBoolean(IS_PREMIUM, false)
    }

    override fun setIsPremium(value: Boolean) {
        with(preferences.edit()) {
            putBoolean(IS_PREMIUM, value)
            commit()
        }
    }

    override fun getIsFirstTime(): Boolean {
        return preferences.getBoolean(IS_FIRST_TIME, false)
    }

    override fun getIsFirstTime(value: Boolean) {
        with(preferences.edit()) {
            putBoolean(IS_FIRST_TIME, value)
            commit()
        }
    }

}