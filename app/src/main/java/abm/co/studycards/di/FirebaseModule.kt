package abm.co.studycards.di

import abm.co.studycards.util.Constants.ADD_CATEGORY_REF
import abm.co.studycards.util.Constants.ADD_WORD_REF
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.WORDS_REF
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

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Named(CATEGORIES_REF)
    fun provideRealtimeDatabaseCategories(db: FirebaseDatabase) =
        db.reference.child(CATEGORIES_REF).apply {
            keepSynced(true)
        }

    @Provides
    @Named(WORDS_REF)
    fun provideRealtimeDatabaseWords(db: FirebaseDatabase) =
        db.reference.child(CATEGORIES_REF).apply {
            keepSynced(true)
        }

    @Provides
    @Named(ADD_CATEGORY_REF)
    fun provideAddCategory(db: FirebaseDatabase) = db.reference.child(CATEGORIES_REF).apply {
        keepSynced(true)
    }

    @Provides
    @Named(ADD_WORD_REF)
    fun provideAddWord(db: FirebaseDatabase) = db.reference.child(CATEGORIES_REF).apply {
        keepSynced(true)
    }
}