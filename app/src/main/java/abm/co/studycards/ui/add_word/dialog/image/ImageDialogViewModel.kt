package abm.co.studycards.ui.add_word.dialog.image

import abm.co.studycards.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageDialogViewModel @Inject constructor(
) : BaseViewModel() {
    var urlText: String? = null
}