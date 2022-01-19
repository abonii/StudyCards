package abm.co.studycards.ui.home

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: Prefs,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference
) : BaseViewModel() {
//
//    private val _stateFlow = MutableStateFlow<CategoryUi>(CategoryUi.Loading)
//    val stateFlow = _stateFlow.asStateFlow()

    var fabMenuOpened = false
    var pressedTime: Long = 0
    var sourceLang = prefs.getSourceLanguage()
        set(value) {
            field = value
            prefs.setSourceLanguage(value)
        }
    var targetLang = prefs.getTargetLanguage()
        set(value) {
            field = value
            prefs.setTargetLanguage(value)
        }

//    init {
//        _stateFlow.value = CategoryUi.Loading
//    }
//
//    fun fetchCategories(){
////        launchIO {
////            when(val wrapper = repository.getCategories()){
////                is ResultWrapper.Error -> {
////                    _stateFlow.value = CategoryUi.Error(wrapper.status)
////                }
////                is ResultWrapper.Success -> {
////                    _stateFlow.value = CategoryUi.Success(wrapper.value)
////                }
////            }
////        }
//    }

}
//sealed class CategoryUi{
//    object Loading: CategoryUi()
//    data class Error(val error:ErrorStatus?):CategoryUi()
//    data class Success(val value:List<Category>):CategoryUi()
//}