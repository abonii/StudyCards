package abm.co.studycards.ui.add_word.dialog.translated

import abm.co.studycards.data.model.oxford.ResultsEntry
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TranslatedWordDialogViewModel @Inject constructor() : ViewModel() {

    var entries: ResultsEntry? = null
    var wordName = ""
    var clickable = true
    var selectedTranslations: MutableList<String> = ArrayList()
    var selectedExamples: MutableSet<String> = HashSet()

    fun loadWord(entry: ResultsEntry?) {
        this.entries = entry
        this.wordName = entries?.id ?: ""
    }

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