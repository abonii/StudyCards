package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.R
import abm.co.studycards.data.model.UserInfoDto.Companion.SELECTED_LANGUAGES_SPLITTER
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.AvailableLanguages
import abm.co.studycards.domain.model.f
import abm.co.studycards.domain.usecases.GetUserInfoUseCase
import abm.co.studycards.domain.usecases.UpdateSelectedLanguageUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageAnyWhereViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateSelectedLanguageUseCase: UpdateSelectedLanguageUseCase,
    private val prefs: Prefs,
    getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel() {

    private var fromTarget = savedStateHandle.get<Boolean>("fromTarget")!!
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    private var listOfRecentlySelectedCodes: MutableSet<String> = mutableSetOf()
    val parentLanguageStateFlow = MutableStateFlow<List<LanguageVHUI>>(emptyList())

    init {
        viewModelScope.launch {
            getUserInfoUseCase().collectLatest {
                val selectedLang = it.selectedLanguages.split(SELECTED_LANGUAGES_SPLITTER)
                val selectedArray = ArrayList<String>(selectedLang)
                if (!selectedLang.contains(prefs.getTargetLanguage())) {
                    selectedArray.add(prefs.getTargetLanguage())
                }
                if (!selectedLang.contains(prefs.getSourceLanguage())) {
                    selectedArray.add(prefs.getSourceLanguage())
                }
                setupLanguages(selectedArray)
            }
        }
    }

    private fun setupLanguages(list: List<String>) {
        val recentlySelected =
            AvailableLanguages.availableLanguages.f(list)
        listOfRecentlySelectedCodes = list.toMutableSet()
        val recentlySelectedLang = mutableListOf<LanguageVHUI>()
        recentlySelectedLang.add(
            LanguageVHUI.TitleLanguages(
                R.string.recently_selected_languages
            )
        )
        recentlySelectedLang.addAll(
            recentlySelected.map {
                LanguageVHUI.Language(LanguageSelectable(it, false))
            })
        recentlySelectedLang.add(
            LanguageVHUI.TitleLanguages(
                R.string.all_languages
            )
        )
        recentlySelectedLang.addAll(
            AvailableLanguages.availableLanguages.map {
                LanguageVHUI.Language(LanguageSelectable(it, false))
            })
        parentLanguageStateFlow.value = recentlySelectedLang
    }

    fun save(selectedLang: LanguageSelectable, onFinish: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!selectedLang.isSelected) {
                makeToast(R.string.u_don_t_selected_word)
                return@launch
            }
            if (fromTarget) {
                prefs.setTargetLanguage(selectedLang.language.code)
            } else {
                prefs.setSourceLanguage(selectedLang.language.code)
            }
            listOfRecentlySelectedCodes.add(selectedLang.language.code)
            updateSelectedLanguageUseCase(
                listOfRecentlySelectedCodes.toTypedArray()
            )
            onFinish.invoke()
        }
    }
}

sealed class LanguageVHUI {
    class TitleLanguages(@StringRes val value: Int) : LanguageVHUI()
    class Language(val value: LanguageSelectable) : LanguageVHUI()
}