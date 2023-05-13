package abm.co.feature.utils

import abm.co.data.di.ApplicationScope
import abm.co.domain.repository.LanguagesRepository
import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@ViewModelScoped
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val languagesRepository: LanguagesRepository
) {

    private var textToSpeech: TextToSpeech? = null
    var isLanguageInstalled: Boolean = false

    init {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            applicationScope.launch {
                if (status != TextToSpeech.ERROR) {
                    val learningLanguage = languagesRepository.getLearningLanguage()
                        .firstOrNull()?.code ?: "en"
                    setLanguageIfAvailable(Locale(learningLanguage))
                }
            }
        }
    }

    private fun setLanguageIfAvailable(locale: Locale) {
        when (textToSpeech?.isLanguageAvailable(locale)) {
            TextToSpeech.LANG_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                textToSpeech?.language = locale
                updateIsLanguageInstalledState()
            }

            else -> {
                isLanguageInstalled = false
            }
        }
    }

    private fun updateIsLanguageInstalledState() {
        val voice = textToSpeech?.voice
        val features = voice?.features
        isLanguageInstalled =
            features != null && !features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
    }

    fun speakAndGet(text: String): Boolean {
        if (!isLanguageInstalled) return false
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        return true
    }

    fun clear() {
        textToSpeech?.apply {
            if (isSpeaking) {
                stop()
            }
            shutdown()
        }
    }
}
