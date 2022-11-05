package abm.co.studycards.domain.model

import abm.co.studycards.R
import android.content.Context

class AvailableLanguages {

    companion object {
        val availableLanguages = listOf(
            Language(
                R.string.arabic, "ar", R.drawable.flag_saudi_arabia
            ), Language(
                R.string.bengali, "bn", R.drawable.flag_bangladesh
            ), Language(
                R.string.chinese, "zh", R.drawable.flag_china
            ), Language(
                R.string.english, "en", R.drawable.flag_united_kingdom
            ), Language(
                R.string.french, "fr", R.drawable.flag_france
            ), Language(
                R.string.german, "de", R.drawable.flag_germany
            ), Language(
                R.string.hindi, "hi", R.drawable.flag_india
            ), Language(
                R.string.indonesian, "id", R.drawable.flag_indonesia
            ), Language(
                R.string.italian, "it", R.drawable.flag_italy
            ), Language(
                R.string.japanese, "ja", R.drawable.flag_japan
            ), Language(
                R.string.kazakh, "kk", R.drawable.flag_kazakhstan
            ), Language(
                R.string.korean, "ko", R.drawable.flag_south_korea
            ), Language(
                R.string.portuguese, "pt", R.drawable.flag_portugal
            ), Language(
                R.string.spanish, "es", R.drawable.flag_spain
            ), Language(
                R.string.turkish, "tr", R.drawable.flag_turkey
            ), Language(
                R.string.ukrainian, "uk", R.drawable.flag_ukraine
            ), Language(
                R.string.russian, "ru", R.drawable.flag_russia
            )
        )
        val systemLanguages = listOf(
            Language(
                R.string.english, "en", R.drawable.flag_united_kingdom
            ), Language(
                R.string.russian, "ru", R.drawable.flag_russia
            )
        )

        fun getLanguageNameByCode(context: Context, c: String): String {
            for (i in availableLanguages) {
                if (i.code == c) {
                    return context.getString(i.languageResCode)
                }
            }
            return ""
        }

        fun getLanguageShortNameByCode(context: Context, c: String): String {
            for (i in availableLanguages) {
                if (i.code == c) {
                    return context.getString(i.languageResCode).take(3)
                }
            }
            return ""
        }

        fun getLanguageDrawableByCode(code: String): Int {
            for (i in availableLanguages) {
                if (i.code == code) {
                    return i.imageFromDrawable
                }
            }
            return availableLanguages[0].imageFromDrawable
        }
    }
}
fun List<Language>.f(codes: List<String>) = filter { lang -> codes.any { it == lang.code } }