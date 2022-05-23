package abm.co.studycards

import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.repository.PricingRepository
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val prefs: Prefs,
    private val firebaseRepository: ServerCloudRepository,
    pricingRepository: PricingRepository
) : BaseViewModel() {

    init {
        pricingRepository.startConnection()
        if (getCurrentUser()?.isAnonymous == false && getCurrentUser()?.isEmailVerified == false) {
            getCurrentUser()?.reload()
        }
    }

    private val sourceLanguage = prefs.getSourceLanguage()
    private val targetLanguage = prefs.getTargetLanguage()

    fun isTargetAndSourceLangSet() = sourceLanguage.isBlank() || targetLanguage.isBlank()

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser


    fun setDailyTranslateTime() {
        firebaseRepository.updateUserInfo()
    }

    fun setUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.addUserName(name)
        }
    }
}