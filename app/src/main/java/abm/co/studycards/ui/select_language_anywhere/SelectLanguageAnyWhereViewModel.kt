package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.f
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.SELECTED_LANGUAGES
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageAnyWhereViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseRepository: ServerCloudRepository,
    private val prefs: Prefs
) : BaseViewModel() {

    private var fromTarget = savedStateHandle.get<Boolean>("fromTarget")!!
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    var listOfRecentlySelectedCodes: MutableSet<String> = mutableSetOf()
    val parentLanguageStateFlow = MutableStateFlow<List<LanguageVHUI>>(emptyList())

    init {
        firebaseRepository.getUserReference()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val list =
                            ((snapshot.child(SELECTED_LANGUAGES).value as String?)
                                ?.split(",") ?: listOf(
                                prefs.getTargetLanguage(),
                                prefs.getSourceLanguage()
                            ))
                        val recentlySelected =
                            AvailableLanguages.availableLanguages.f(list)
                        listOfRecentlySelectedCodes = list.toMutableSet()
                        val recentlySelectedLang = mutableListOf<LanguageVHUI>()
                        recentlySelectedLang.add(
                            LanguageVHUI.TitleLanguages(
                                App.instance.getString(R.string.recently_selected_languages)
                            )
                        )
                        recentlySelectedLang.addAll(
                            recentlySelected.map {
                                LanguageVHUI.Language(LanguageSelectable(it, false))
                            })
                        recentlySelectedLang.add(
                            LanguageVHUI.TitleLanguages(
                                App.instance.getString(R.string.all_languages)
                            )
                        )
                        recentlySelectedLang.addAll(
                            AvailableLanguages.availableLanguages.map {
                                LanguageVHUI.Language(LanguageSelectable(it, false))
                            })
                        parentLanguageStateFlow.value = recentlySelectedLang
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun save(selectedLang: LanguageSelectable, onFinish: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!selectedLang.isSelected) {
                makeToast(App.instance.getString(R.string.u_don_t_selected_word))
                return@launch
            }
            if (fromTarget) {
                prefs.setTargetLanguage(selectedLang.language.code)
            } else {
                prefs.setSourceLanguage(selectedLang.language.code)
            }
            listOfRecentlySelectedCodes.add(selectedLang.language.code)
            firebaseRepository.updateSelectedLanguages(
                *listOfRecentlySelectedCodes.toTypedArray()
            )
            onFinish.invoke()
        }
    }
}

sealed class LanguageVHUI {
    class TitleLanguages(val value: String) : LanguageVHUI()
    class Language(val value: LanguageSelectable) : LanguageVHUI()
}