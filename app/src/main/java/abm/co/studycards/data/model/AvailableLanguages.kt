package abm.co.studycards.data.model

import abm.co.studycards.R
import android.content.Context

class AvailableLanguages {

    companion object {
        val availableLanguages = listOf(
            Language(
                R.string.arabic, "ar", R.drawable.flag_arabic
            ), Language(
                R.string.chinese, "zh", R.drawable.flag_chinese
            ), Language(
                R.string.english, "en", R.drawable.flag_england
            ), Language(
                R.string.french, "fr", R.drawable.flag_french
            ), Language(
                R.string.german, "de", R.drawable.flag_german
            ), Language(
                R.string.italian, "it", R.drawable.flag_italian
            ), Language(
                R.string.kazakh, "kk", R.drawable.flag_kazakhstan
            ), Language(
                R.string.korean, "ko", R.drawable.flag_korean
            ), Language(
                R.string.turkish, "tr", R.drawable.flag_turkish
            ), Language(
                R.string.russian, "ru", R.drawable.flag_russian
            )
        )
        val systemLanguages = listOf(
            Language(
                R.string.english, "en", R.drawable.flag_england
            ), Language(
                R.string.russian, "ru", R.drawable.flag_russian
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