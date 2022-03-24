package abm.co.studycards.ui.games.confirmend

import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ConfirmEndViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
    val confirmType = savedStateHandle.get<ConfirmText>("confirmType")
}