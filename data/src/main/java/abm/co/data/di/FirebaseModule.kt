package abm.co.data.di

import abm.co.data.datastore.LanguagesDataStore
import abm.co.data.model.DatabaseRef.CATEGORY_REF
import abm.co.data.model.DatabaseRef.CONFIG_REF
import abm.co.data.model.DatabaseRef.ROOT_REF
import abm.co.data.model.DatabaseRef.USER_ID
import abm.co.data.model.DatabaseRef.USER_PROPERTIES_REF
import abm.co.data.model.DatabaseRef.USER_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFunctionsInstance(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Named(USER_ID)
    @Provides
    fun provideUserId(firebaseAuth: FirebaseAuth) = firebaseAuth.currentUser?.uid ?: "no-user-id"

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Named(ROOT_REF)
    @Provides
    fun provideRootReference(db: FirebaseDatabase): DatabaseReference = db.reference.child(ROOT_REF)

    @Singleton
    @Provides
    @Named(CONFIG_REF)
    fun provideApiKeys(@Named(ROOT_REF) root: DatabaseReference):
        DatabaseReference = root.child(CONFIG_REF)

//    @Provides
//    @Named(CATEGORY_REF)
//    fun provideRealtimeDatabaseCategories(
//        @Named(ROOT_REF) root: DatabaseReference,
//        @Named(USER_ID) userId: String,
//        languagesDataStore: LanguagesDataStore
//    ): DatabaseReference {
//        FirebaseDatabase.getInstance().reference
//        return combine(
//            languagesDataStore.getLearningLanguage(),
//            languagesDataStore.getNativeLanguage()
//        ) { learning, native ->
//            val a =root.child(USER_REF).child(userId)
//                .child(CATEGORY_REF)
//                .child(native?.code ?: "en")
//                .child(learning?.code ?: "en")
//                .apply { keepSynced(true) }
//            return@combine a
//        }
//    }

    @Named(USER_PROPERTIES_REF)
    @Provides
    fun provideRealtimeDatabaseUser(
        @Named(ROOT_REF) root: DatabaseReference,
        @Named(USER_ID) userId: String
    ): DatabaseReference {
        return root.child(USER_REF).child(userId).child(USER_PROPERTIES_REF)
            .apply { keepSynced(true) }
    }
}