package abm.co.data.di

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

    @Named(USER_PROPERTIES_REF)
    @Provides
    fun provideRealtimeDatabaseUser(
        @Named(ROOT_REF) root: DatabaseReference,
        firebaseAuth: FirebaseAuth
    ): DatabaseReference {
        return root.child(USER_REF)
            .child(firebaseAuth.currentUser?.uid ?: "no-user-id")
            .child(USER_PROPERTIES_REF)
            .apply { keepSynced(true) }
    }
}