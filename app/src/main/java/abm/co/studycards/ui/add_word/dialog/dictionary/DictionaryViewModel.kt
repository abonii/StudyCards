package abm.co.studycards.ui.add_word.dialog.dictionary

import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var entries: ResultsEntry? = savedStateHandle.get("entry")
    var fromTarget = savedStateHandle.get<Boolean>("from_target") ?: false
    var wordName = entries?.word ?: ""
    var clickable = true
    var selectedTranslations: MutableList<String> = ArrayList()
    var selectedExamples: MutableSet<String> = HashSet()

    fun onExampleSelected(example: String, isPressed: Boolean) {
        if (isPressed) {
            selectedExamples.remove(example)
        } else {
            selectedExamples.add(example)
        }
    }

    fun onTranslationSelected(translation: String, isPressed: Boolean) {
        if (isPressed) {
            selectedTranslations.remove(translation)
        } else {
            selectedTranslations.add(translation)
        }
    }
}