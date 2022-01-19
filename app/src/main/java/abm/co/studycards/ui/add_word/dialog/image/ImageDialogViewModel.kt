package abm.co.studycards.ui.add_word.dialog.image

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageDialogViewModel @Inject constructor(
): ViewModel() {
    var urlText:String? = null
}