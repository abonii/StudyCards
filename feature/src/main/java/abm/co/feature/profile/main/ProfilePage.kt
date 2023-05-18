package abm.co.feature.profile.main

import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.SecondaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.extensions.collectInLifecycle
import abm.co.designsystem.extensions.getActivity
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.authorization.common.TrailingIcon
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.utils.AnalyticsManager
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun ProfilePage(
    navigateToStorePage: () -> Unit,
    navigateToAuthorization: () -> Unit,
    navigateToChangePasswordPage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "profile_page_viewed"
        )
    }
    val startGoogleLoginForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = viewModel::firebaseAuthWithGoogle
    )
    val state by viewModel.state.collectAsState()
    val activity = getActivity()
    viewModel.channel.collectInLifecycle { channel ->
        when (channel) {
            is ProfileContractChannel.ConnectWithGoogleAccount -> {
                startGoogleLoginForResult.launch(channel.intent)
            }

            ProfileContractChannel.NavigateToAuthorization -> {
                navigateToAuthorization()
            }

            ProfileContractChannel.NavigateToStore -> {
                navigateToStorePage()
            }

            is ProfileContractChannel.NavigateToTelegramApp -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(channel.url)
                activity?.startActivity(i)
            }

            ProfileContractChannel.ReopenTheApp -> {
                activity?.let {
                    it.startActivity(
                        Intent(it, it::class.java)
                    )
                    it.finish()
                }
            }

            ProfileContractChannel.NavigateToChangePassword -> {
                navigateToChangePasswordPage()
            }

            is ProfileContractChannel.ShowMessage -> showMessage(channel.messageContent)
        }
    }

    SetStatusBarColor()
    ProfileScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun ProfileScreen(
    state: ProfileContractState,
    onEvent: (ProfileContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar()
        ScrollableContent(
            userInfo = state.userInfo,
            settings = state.settings,
            onEvent = onEvent,
            appVersion = state.appVersion,
            modifier = Modifier.weight(1f)
        )
        DialogContent(
            state = state.dialog,
            onEvent = onEvent
        )
    }
}

@Composable
private fun DialogContent(
    state: ProfileContractState.Dialog,
    onEvent: (ProfileContractEvent.Dialog) -> Unit,
    modifier: Modifier = Modifier
) {
    state.appLanguage?.let { languages ->
        Dialog(
            onDismissRequest = {
                onEvent(ProfileContractEvent.Dialog.OnDismissDialog)
            },
            content = {
                Column(
                    modifier = modifier
                        .background(
                            color = StudyCardsTheme.colors.backgroundPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    languages.forEach { languageUI ->
                        LanguageItem(
                            modifier = Modifier,
                            language = languageUI,
                            onClick = {
                                onEvent(ProfileContractEvent.Dialog.OnClickAppLanguage(languageUI))
                            }
                        )
                        Divider()
                    }
                }
            }
        )
    }
}

@Composable
private fun LanguageItem(
    language: LanguageUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = language.flagFromDrawable),
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = language.languageNameResCode),
            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
            color = StudyCardsTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ScrollableContent(
    userInfo: ProfileContractState.UserInfo?,
    settings: ProfileContractState.Settings,
    appVersion: String,
    onEvent: (ProfileContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
        ) {
            when (userInfo) {
                is ProfileContractState.UserInfo.Anonymous -> {
                    AnonymousUserContent(
                        anonymous = userInfo,
                        onEvent = onEvent
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                is ProfileContractState.UserInfo.Signed -> {
                    SignedUserContent(
                        signed = userInfo
                    )
                }

                null -> Spacer(modifier = Modifier.height(30.dp))
            }
        }
        SettingsContent(
            modifier = Modifier.weight(1f),
            settings = settings,
            onEvent = onEvent
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            text = stringResource(
                id = R.string.Profile_Version_title,
                appVersion,
                appVersion
            ),
            style = StudyCardsTheme.typography.weight400Size12LineHeight16,
            textAlign = TextAlign.Center,
            color = StudyCardsTheme.colors.textSecondary
        )
    }
}

@Composable
private fun AnonymousUserContent(
    anonymous: ProfileContractState.UserInfo.Anonymous,
    onEvent: (ProfileContractEvent.Anonymous) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.Profile_Anonymous_title),
            style = StudyCardsTheme.typography.weight600Size16LineHeight18,
            textAlign = TextAlign.Center,
            color = StudyCardsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        SecondaryButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            title = stringResource(id = R.string.Profile_Anonymous_Google_signIn),
            onClick = {
                onEvent(ProfileContractEvent.Anonymous.OnClickSignUpWithGoogle)
            },
            startIcon = R.drawable.ic_google
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.Profile_Anonymous_or),
            style = StudyCardsTheme.typography.weight400Size12LineHeight16,
            textAlign = TextAlign.Center,
            color = StudyCardsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextFieldWithLabel(
            label = stringResource(id = R.string.LoginPage_EmailTitle),
            hint = stringResource(id = R.string.LoginPage_EmailHint),
            value = anonymous.email,
            onValueChange = {
                onEvent(ProfileContractEvent.Anonymous.OnEnterEmail(it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        var showPassword by remember { mutableStateOf(value = false) }
        TextFieldWithLabel(
            label = stringResource(id = R.string.LoginPage_PasswordTitle),
            hint = stringResource(id = R.string.LoginPage_PasswordHint),
            value = anonymous.password,
            onValueChange = {
                onEvent(ProfileContractEvent.Anonymous.OnEnterPassword(it))
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TrailingIcon(
                    showPassword = showPassword,
                    onClick = {
                        showPassword = it
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryButton(
            title = stringResource(id = R.string.Profile_Anonymous_Email_login),
            onClick = {
                onEvent(ProfileContractEvent.Anonymous.OnClickSignUp)
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            buttonState = if (anonymous.isSignUpButtonLoading) ButtonState.Loading
            else ButtonState.Normal
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SignedUserContent(
    signed: ProfileContractState.UserInfo.Signed,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        signed.photoUri?.let {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(90.dp),
                model = it,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
        signed.username?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                textAlign = TextAlign.Center,
                color = StudyCardsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        signed.email?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                textAlign = TextAlign.Center,
                color = StudyCardsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(!signed.isVerified){
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.Profile_NotVerified_title),
                style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                textAlign = TextAlign.Center,
                color = StudyCardsTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsContent(
    settings: ProfileContractState.Settings,
    onEvent: (ProfileContractEvent.Settings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.Profile_Settings_title),
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            color = StudyCardsTheme.colors.onyx
        )
        SettingsItem(
            title = stringResource(id = R.string.Profile_Settings_Store_title),
            description = settings.translationCount.toString(),
            color = StudyCardsTheme.colors.buttonPrimary,
            onClick = {
                onEvent(ProfileContractEvent.Settings.OnClickStore)
            }
        )
        Divider()
        SettingsItem(
            title = stringResource(id = R.string.Profile_Settings_Langauge_title),
            description = settings.selectedAppLanguage?.languageNameResCode?.let {
                stringResource(id = it)
            } ?: stringResource(id = R.string.Profile_Settings_Langauge_notSelected),
            onClick = {
                onEvent(ProfileContractEvent.Settings.OnClickAppLanguage)
            }
        )
        if (settings.showSendConfirmation) {
            Divider()
            SettingsItem(
                title = stringResource(id = R.string.Profile_Settings_SendConfirmation_title),
                onClick = {
                    onEvent(ProfileContractEvent.Settings.OnClickSendConfirmation)
                }
            )
        }
        if (settings.showChangePassword) {
            Divider()
            SettingsItem(
                title = stringResource(id = R.string.Profile_Settings_ChangePassword_title),
                onClick = {
                    onEvent(ProfileContractEvent.Settings.OnClickChangePassword)
                }
            )
        }
        Divider()
        SettingsItem(
            title = stringResource(id = R.string.Profile_Settings_Contact_title),
            onClick = {
                onEvent(ProfileContractEvent.Settings.OnClickContactUs)
            }
        )
        Divider()
        SettingsItem(
            title = stringResource(id = R.string.Profile_Settings_SignOut_title),
            onClick = {
                onEvent(ProfileContractEvent.Settings.OnClickSignOut)
            }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    color: Color = StudyCardsTheme.colors.textSecondary
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f),
            text = title,
            style = StudyCardsTheme.typography.weight400Size14LineHeight24,
            color = color
        )
        description?.let {
            Text(
                text = description,
                style = StudyCardsTheme.typography.weight400Size14LineHeight24,
                color = color
            )
        } ?: Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = color
        )
    }
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.Profile_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
