package abm.co.data.pref

import abm.co.data.model.user.LanguageDTO
import abm.co.data.model.user.toDTO
import abm.co.data.model.user.toDomain
import abm.co.domain.model.Language
import abm.co.domain.prefs.Prefs
import abm.co.domain.prefs.Prefs.Companion.APP_LANGUAGE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class PrefsImpl @Inject constructor(
    private val preferences: SharedPreferences,
    private val gson: Gson
) : Prefs {

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
}
