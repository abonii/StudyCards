package abm.co.studycards.ui.language

import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(
    val prefs: Prefs,
    val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {
    var nativeLanguageCode = ""
    var targetLanguageCode = ""

    fun onSaveLanguages() {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setSourceLanguage(nativeLanguageCode)
            prefs.setTargetLanguage(targetLanguageCode)
            firebaseRepository.setSelectedLanguages(nativeLanguageCode, targetLanguageCode)
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