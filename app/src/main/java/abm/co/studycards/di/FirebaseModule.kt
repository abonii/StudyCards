package abm.co.studycards.di

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.Constants.USER_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Named(USER_ID)
    @Provides
    fun provideUserId() = FirebaseAuth.getInstance().uid ?: ""

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

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
    @Provides
    @Named(USERS_REF)
    fun provideRealtimeDatabaseUser(
        db: FirebaseDatabase,
        @Named(USER_ID) userId: String
    ) =
        db.reference.child(USERS_REF).child(userId).apply {
                keepSynced(true)
            }
}