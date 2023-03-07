package abm.co.data.di

import abm.co.data.BuildConfig
import abm.co.data.R
import abm.co.data.model.DatabaseReferenceType.CATEGORIES_REF
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.EXPLORE_REF
import abm.co.data.model.DatabaseReferenceType.SETS_REF
import abm.co.data.model.DatabaseReferenceType.USER_ID
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.domain.prefs.Prefs
import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseFunctionsInstance(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_SERVER_ID)
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
    fun provideUserId(firebaseAuth: FirebaseAuth) = firebaseAuth.currentUser?.uid ?: "no-user"

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Named(CONFIG_REF)
    fun provideApiKeys(db: FirebaseDatabase): DatabaseReference = db.reference.child(CONFIG_REF)

    @Provides
    @Named(CATEGORIES_REF)
    fun provideRealtimeDatabaseCategories(
        db: FirebaseDatabase,
        @Named(USER_ID) userId: String,
        prefs: Prefs
    ): DatabaseReference {
        return db.reference.child(USER_REF).child(userId)
            .child(CATEGORIES_REF)
            .child("${prefs.getSourceLanguage()}-${prefs.getTargetLanguage()}")
            .apply {
                keepSynced(true)
            }

    }

    @Named(USER_REF)
    @Provides
    fun provideRealtimeDatabaseUser(
        db: FirebaseDatabase,
        @Named(USER_ID) userId: String
    ): DatabaseReference {
        return db.reference.child(USER_REF).child(userId)
            .apply { keepSynced(true) }

    }

    @Named(EXPLORE_REF)
    @Provides
    fun provideRealtimeDatabaseExplore(
        db: FirebaseDatabase,
        prefs: Prefs
    ): DatabaseReference {
        return db.reference.child(EXPLORE_REF)
            .child("${prefs.getSourceLanguage()}-${prefs.getTargetLanguage()}")
            .child(SETS_REF).apply {
                keepSynced(true)
            }
    }
}