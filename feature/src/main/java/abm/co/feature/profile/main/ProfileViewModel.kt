package abm.co.feature.profile.main

import abm.co.core.appinfo.ApplicationInfo
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.feature.R
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.defaultLanguages
import abm.co.feature.userattributes.lanugage.filterByCodes
import abm.co.feature.userattributes.lanugage.toDomain
import abm.co.feature.userattributes.lanugage.toUI
import abm.co.feature.utils.LocaleHelper
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val languagesRepository: LanguagesRepository,
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val authorizationRepository: AuthorizationRepository,
    private val serverRepository: ServerRepository,
    private val applicationInfo: ApplicationInfo
) : ViewModel() {

    private val _channel = Channel<ProfileContractChannel>()
    val channel: Flow<ProfileContractChannel> = _channel.receiveAsFlow()

    private val _state: MutableStateFlow<ProfileContractState> = MutableStateFlow(
        ProfileContractState(
            appVersion = applicationInfo.getVersionName()
        )
    )
    val state: StateFlow<ProfileContractState> = _state.asStateFlow()

    private val currentUser get() = firebaseAuth.currentUser

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            serverRepository.getUser.collectLatest { either ->
                either.onSuccess { user ->
                    _state.value =
                        ProfileContractState(
                            userInfo = if (currentUser?.isAnonymous == true) {
                                ProfileContractState.UserInfo.Anonymous()
                            } else {
                                ProfileContractState.UserInfo.Signed(
                                    isVerified = currentUser?.isEmailVerified ?: false,
                                    email = user?.email,
                                    photoUri = currentUser?.photoUrl,
                                    username = user?.name
                                )
                            },
                            settings = ProfileContractState.Settings(
                                selectedAppLanguage = languagesRepository.getAppLanguage()?.toUI(),
                                translationCount = user?.translateCounts ?: 0,
                                showSendConfirmation = currentUser?.let { cUser ->
                                    !cUser.isAnonymous && !cUser.isEmailVerified
                                } ?: false,
                                showChangePassword = currentUser?.isEmailVerified == true
                            ),
                            appVersion = applicationInfo.getVersionName()
                        )
                }.onFailure {
                    it.sendException()
                }
            }
        }
    }

    fun onEvent(event: ProfileContractEvent) {
        when (event) {
            ProfileContractEvent.Anonymous.OnClickSignUp -> {
                (state.value.userInfo as? ProfileContractState.UserInfo.Anonymous)
                    ?.onClickRegistration()
            }

            ProfileContractEvent.Anonymous.OnClickSignUpWithGoogle -> {
                connectViaGoogle()
            }

            is ProfileContractEvent.Anonymous.OnEnterEmail -> {
                _state.update { oldState ->
                    oldState.copy(
                        userInfo = when (val userInfo = oldState.userInfo) {
                            is ProfileContractState.UserInfo.Anonymous -> {
                                userInfo.copy(
                                    email = event.value
                                )
                            }

                            else -> userInfo
                        }
                    )
                }
            }

            is ProfileContractEvent.Anonymous.OnEnterPassword -> {
                _state.update { oldState ->
                    oldState.copy(
                        userInfo = when (val userInfo = oldState.userInfo) {
                            is ProfileContractState.UserInfo.Anonymous -> {
                                userInfo.copy(
                                    password = event.value
                                )
                            }

                            else -> userInfo
                        }
                    )
                }
            }

            ProfileContractEvent.Settings.OnClickAppLanguage -> {
                _state.update { oldState ->
                    oldState.copy(
                        dialog = ProfileContractState.Dialog(
                            appLanguage = defaultLanguages.filterByCodes(
                                codes = listOf(
                                    "en",
                                    "ru",
                                    "kz"
                                )
                            )
                        )
                    )
                }
            }

            ProfileContractEvent.Settings.OnClickStore -> {
                viewModelScope.launch {
                    _channel.send(ProfileContractChannel.NavigateToStore)
                }
            }

            ProfileContractEvent.Settings.OnClickContactUs -> {
                viewModelScope.launch {
                    _channel.send(
                        ProfileContractChannel.NavigateToTelegramApp(
                            url = "https://t.me/studycardsdev"
                        )
                    )
                }
            }

            ProfileContractEvent.Settings.OnClickSignOut -> {
                onClickSignOut()
            }

            is ProfileContractEvent.Dialog.OnClickAppLanguage -> {
                viewModelScope.launch {
                    setAppLanguage(event.languageUI)
                    _channel.send(ProfileContractChannel.ReopenTheApp)
                }
            }

            ProfileContractEvent.Settings.OnClickChangePassword -> {
                viewModelScope.launch {
                    _channel.send(
                        ProfileContractChannel.NavigateToChangePassword
                    )
                }
            }

            ProfileContractEvent.Settings.OnClickSendConfirmation -> {
                sendVerificationEmail()
            }

            ProfileContractEvent.Dialog.OnDismissDialog -> {
                _state.update { oldState ->
                    oldState.copy(
                        dialog = ProfileContractState.Dialog()
                    )
                }
            }
        }
    }

    private fun connectViaGoogle() {
        viewModelScope.launch {
            val intent = Intent(googleSignInClient.signInIntent)
            _channel.send(ProfileContractChannel.ConnectWithGoogleAccount(intent))
        }
    }


    private suspend fun setAppLanguage(language: LanguageUI) {
        languagesRepository.setAppLanguage(language.toDomain())
        LocaleHelper.setLocale(applicationContext, language.code)
        _channel.send(ProfileContractChannel.ReopenTheApp)
    }

    private fun ProfileContractState.UserInfo.Anonymous.createUserFromAnonymous() {
        updateSignUpButtonLoadingState(true)
        val credential = EmailAuthProvider.getCredential(email, password)
        currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    sendVerificationEmail()
                    updateUser()
                } else {
                    it.exception?.mapToFailure()?.sendException()
                }
                updateSignUpButtonLoadingState(false)
            }
    }

    private fun updateUser() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser?.let { user ->
                user.reload()
                saveUserInfo(email = user.email, name = user.displayName)
                _state.update { oldState ->
                    oldState.copy(
                        userInfo = if (user.isAnonymous) {
                            ProfileContractState.UserInfo.Anonymous()
                        } else {
                            ProfileContractState.UserInfo.Signed(
                                email = user.email,
                                isVerified = user.isEmailVerified,
                                photoUri = user.photoUrl
                            )
                        },
                        settings = oldState.settings.copy(
                            showSendConfirmation = currentUser?.let { cUser ->
                                !cUser.isAnonymous && !cUser.isEmailVerified
                            } ?: false,
                            showChangePassword = currentUser?.isEmailVerified == true
                        )
                    )
                }
            }
        }
    }

    fun firebaseAuthWithGoogle(activityResult: ActivityResult) {
        try {
            if (activityResult.resultCode == Activity.RESULT_OK) {
                activityResult.data?.let { intent ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        account.idToken?.let {
                            val credential = GoogleAuthProvider.getCredential(it, null)
                            currentUser?.linkWithCredential(credential)
                                ?.addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        task.exception?.mapToFailure()?.sendException()
                                    } else {
                                        updateUser()
                                    }
                                }
                        }
                    } catch (e: ApiException) {
                        e.mapToFailure().sendException()
                    }
                }
            }
        } catch (e: ApiException) {
            e.mapToFailure().sendException()
        }
    }

    private fun ProfileContractState.UserInfo.Anonymous.onClickRegistration() {
        val email = this.email.trim()
        val password = this.password.trim()
        when {
            TextUtils.isEmpty(email) -> {
                Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_EmailEmpty))
                    .sendException()
            }

            TextUtils.isEmpty(password) -> {
                Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_PasswordEmpty))
                    .sendException()
            }

            password.length < 6 -> {
                Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_PasswordLengthNotCorrect))
                    .sendException()
            }

            else -> {
                createUserFromAnonymous()
            }
        }
    }

    private fun onClickSignOut() {
        viewModelScope.launch {
            when (state.value.userInfo) {
                is ProfileContractState.UserInfo.Anonymous -> {
                    removeDatabaseOfUser {
                        _channel.send(
                            ProfileContractChannel.NavigateToAuthorization
                        )
                    }
                }

                else -> {
                    simpleLogout {
                        _channel.send(
                            ProfileContractChannel.NavigateToAuthorization
                        )
                    }
                }
            }
        }
    }

    private suspend fun removeDatabaseOfUser(onFinish: suspend () -> Unit) {
        serverRepository.removeUserDatabase()
        signOut(onFinish)
    }

    private suspend fun signOut(onFinish: suspend () -> Unit) {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
            .addOnCompleteListener {
                viewModelScope.launch {
                    currentUser?.delete()
                    delay(100) // not needed
                    onFinish()
                }
            }
    }

    private fun sendVerificationEmail() {
        currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _channel.send(
                            ProfileContractChannel.ShowMessage(
                                MessageContent.Snackbar.MessageContentRes(
                                    titleRes = abm.co.designsystem.R.string.Messages_OK,
                                    subtitleRes = abm.co.designsystem.R.string.we_send_verification,
                                    type = MessageType.Success
                                )
                            )
                        )
                    } else {
                        task.exception?.mapToFailure()?.sendException()
                    }
                }
            }
    }

    private fun simpleLogout(onFinish: suspend () -> Unit) {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
            .addOnCompleteListener {
                viewModelScope.launch {
                    delay(100) // not needed
                    onFinish()
                }
            }
    }

    private fun saveUserInfo(
        name: String? = null,
        email: String? = null,
        password: String? = null
    ) {
        viewModelScope.launch {
            authorizationRepository.setUserInfo(
                name = name,
                email = email,
                password = password
            )
        }
    }

    private fun updateSignUpButtonLoadingState(isLoading: Boolean) {
        _state.update { oldState ->
            oldState.copy(
                userInfo = (oldState.userInfo as? ProfileContractState.UserInfo.Anonymous)
                    ?.copy(isSignUpButtonLoading = isLoading) ?: oldState.userInfo
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ProfileContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
data class ProfileContractState(
    val settings: Settings = Settings(),
    val userInfo: UserInfo? = null,
    val dialog: Dialog = Dialog(),
    val appVersion: String
) {
    @Immutable
    sealed interface UserInfo {
        data class Anonymous(
            val email: String = "",
            val password: String = "",
            val isSignUpButtonLoading: Boolean = false
        ) : UserInfo

        data class Signed(
            val isVerified: Boolean,
            val email: String? = null,
            val photoUri: Uri? = null,
            val username: String? = null
        ) : UserInfo
    }

    @Immutable
    data class Settings(
        val selectedAppLanguage: LanguageUI? = null,
        val translationCount: Long = 0,
        val showSendConfirmation: Boolean = false,
        val showChangePassword: Boolean = false
    )

    @Immutable
    data class Dialog(
        val appLanguage: List<LanguageUI>? = null
    )
}

@Immutable
sealed interface ProfileContractEvent {
    sealed interface Anonymous : ProfileContractEvent {
        data class OnEnterEmail(val value: String) : Anonymous
        data class OnEnterPassword(val value: String) : Anonymous
        object OnClickSignUp : Anonymous
        object OnClickSignUpWithGoogle : Anonymous
    }

    sealed interface Settings : ProfileContractEvent {
        object OnClickStore : Settings
        object OnClickAppLanguage : Settings
        object OnClickContactUs : Settings
        object OnClickSendConfirmation : Settings
        object OnClickChangePassword : Settings
        object OnClickSignOut : Settings
    }

    sealed interface Dialog : ProfileContractEvent {
        data class OnClickAppLanguage(val languageUI: LanguageUI) : Dialog
        object OnDismissDialog : Dialog
    }
}

@Immutable
sealed interface ProfileContractChannel {
    data class ConnectWithGoogleAccount(val intent: Intent) : ProfileContractChannel
    object NavigateToAuthorization : ProfileContractChannel
    object NavigateToStore : ProfileContractChannel
    object ReopenTheApp : ProfileContractChannel
    object NavigateToChangePassword : ProfileContractChannel
    data class NavigateToTelegramApp(val url: String) : ProfileContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ProfileContractChannel
}
