package abm.co.studycards.ui.language

import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.AvailableLanguages
import abm.co.studycards.domain.model.Language
import abm.co.studycards.domain.usecases.AddSelectedLanguageUseCase
import abm.co.studycards.ui.select_language_anywhere.LanguageSelectable
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(
    val prefs: Prefs,
    private val addSelectedLanguageUseCase: AddSelectedLanguageUseCase
) : BaseViewModel() {

    private var nativeLanguageCode = ""
    private var targetLanguageCode = ""

    fun getAvailableLanguages() = AvailableLanguages.availableLanguages.map {
        LanguageSelectable(it, false)
    }

    fun onSaveLanguages() {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setSourceLanguage(nativeLanguageCode)
            prefs.setTargetLanguage(targetLanguageCode)
            addSelectedLanguageUseCase(nativeLanguageCode, targetLanguageCode)
        }
    }

    fun onClickWithCode(lang: Language, isTargetLanguage: Boolean) {
        if (isTargetLanguage) {
            targetLanguageCode = lang.code
        } else {
            nativeLanguageCode = lang.code
        }
    }

    fun isSelectedCorrectly(): Boolean {
        return nativeLanguageCode.isNotBlank()
                && targetLanguageCode.isNotBlank()
                && nativeLanguageCode != targetLanguageCode
    }

}