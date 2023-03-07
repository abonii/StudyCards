package abm.co.studycards.ui.games.confirmend

import abm.co.studycards.domain.model.ConfirmText
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmEndViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val confirmType = savedStateHandle.get<ConfirmText>("confirmType")
}