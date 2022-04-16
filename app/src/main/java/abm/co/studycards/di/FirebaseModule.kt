package abm.co.studycards.di

import abm.co.studycards.R
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants.API_KEYS
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.SETS_REF
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.Constants.USER_ID
import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideGoogleSignInOptions(application: Application): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Provides
    fun provideGoogleSignInClient(
        signInOptions: GoogleSignInOptions,
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, signInOptions)
    }

    @Named(USER_ID)
    @Provides
    fun provideUserId() = FirebaseAuth.getInstance().uid ?: ""

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Named(API_KEYS)
    fun provideApiKeys(db: FirebaseDatabase) = db.reference.child("api")

    @Provides
    @Named(CATEGORIES_REF)
    fun provideRealtimeDatabaseCategories(
        db: FirebaseDatabase,
        @Named(USER_ID) userId: String,
        prefs: Prefs,
    ) =
        db.reference.child(USERS_REF).child(userId)
            .child("${prefs.getSourceLanguage()}-${prefs.getTargetLanguage()}")
            .child(CATEGORIES_REF).apply {
                keepSynced(true)
            }

    @Named(USERS_REF)
    @Provides
    fun provideRealtimeDatabaseUser(
        db: FirebaseDatabase,
        @Named(USER_ID) userId: String
    ) =
        db.reference.child(USERS_REF).child(userId).apply {
            keepSynced(true)
        }

    @Named(EXPLORE_REF)
    @Provides
    fun provideRealtimeDatabaseExplore(
        db: FirebaseDatabase,
        prefs: Prefs
    ) = db.reference.child(EXPLORE_REF).child(SETS_REF)
        .child("${prefs.getSourceLanguage()}-${prefs.getTargetLanguage()}").apply {
            keepSynced(true)
        }
}