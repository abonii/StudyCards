package abm.co.studycards.ui.profile

import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: Prefs
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
    val appLanguage = prefs.getAppLanguage()
    val targetLang = prefs.getTargetLanguage()

    fun setAppLanguage(language: Language) {
        val lang = prefs.getAppLanguage()
        if (lang != language.code) {
            prefs.setAppLanguage(language.code)
        }
    }

}