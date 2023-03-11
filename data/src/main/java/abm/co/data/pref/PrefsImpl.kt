package abm.co.data.pref

import abm.co.data.model.user.LanguageDTO
import abm.co.data.model.user.toDTO
import abm.co.data.model.user.toDomain
import abm.co.domain.model.Language
import abm.co.domain.prefs.Prefs
import abm.co.domain.prefs.Prefs.Companion.ACCESS_TOKEN
import abm.co.domain.prefs.Prefs.Companion.IS_FIRST_TIME
import abm.co.domain.prefs.Prefs.Companion.IS_PREMIUM
import abm.co.domain.prefs.Prefs.Companion.APP_LANGUAGE
import abm.co.domain.prefs.Prefs.Companion.NATIVE_LANGUAGE
import abm.co.domain.prefs.Prefs.Companion.LEARNING_LANGUAGE
import abm.co.domain.prefs.Prefs.Companion.USER_ID
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class PrefsImpl @Inject constructor(
    private val preferences: SharedPreferences,
    private val gson: Gson
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

    override fun getAppLanguage(): Language? {
        val json = try {
            preferences.getString(APP_LANGUAGE, null)
        } catch (e: Throwable) {
            return null
        }
        return try {
            val type: Type = object : TypeToken<LanguageDTO?>() {}.type
            val language = gson.fromJson<LanguageDTO>(json, type)
            language?.toDomain()
        } catch (e: Throwable) {
            null
        }
    }

    override fun setAppLanguage(value: Language) {
        val json = gson.toJson(value.toDTO())
        preferences.edit().putString(APP_LANGUAGE, json).apply()
    }

    override fun getNativeLanguage(): Language? {
        val json = try {
            preferences.getString(NATIVE_LANGUAGE, null)
        } catch (e: Throwable) {
            return null
        }
        return try {
            val type: Type = object : TypeToken<LanguageDTO?>() {}.type
            val language = gson.fromJson<LanguageDTO>(json, type)
            language?.toDomain()
        } catch (e: Throwable) {
            null
        }
    }

    override fun setNativeLanguage(value: Language) {
        val json = gson.toJson(value.toDTO())
        preferences.edit().putString(NATIVE_LANGUAGE, json).apply()
    }

    override fun getLearningLanguage(): Language? {
        val json = try {
            preferences.getString(LEARNING_LANGUAGE, null)
        } catch (e: Throwable) {
            return null
        }
        return try {
            val type: Type = object : TypeToken<LanguageDTO?>() {}.type
            val language = gson.fromJson<LanguageDTO>(json, type)
            language?.toDomain()
        } catch (e: Throwable) {
            null
        }
    }

    override fun setLearningLanguage(value: Language) {
        val json = gson.toJson(value.toDTO())
        preferences.edit().putString(LEARNING_LANGUAGE, json).apply()
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