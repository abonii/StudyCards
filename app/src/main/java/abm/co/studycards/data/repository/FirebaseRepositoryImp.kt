package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants.API_REF
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_EVERY_DAY
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.SELECTED_LANGUAGES
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.Constants.USER_REF
import abm.co.studycards.util.Constants.WORDS_REF
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @Named(EXPLORE_REF) private val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(USERS_REF) private var userDbRef: DatabaseReference,
    @Named(USER_REF) private var rootUserDbRef: DatabaseReference,
    @Named(API_REF) private var apiKeys: DatabaseReference
) : ServerCloudRepository {

    private val _error = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val error = _error.asSharedFlow()

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getCurrentUser() = Firebase.auth.currentUser

    override suspend fun updateCategoryName(category: Category) {
        withContext(Dispatchers.IO) {
            categoriesDbRef.child(category.id)
                .updateChildren(mapOf(Category.MAIN_NAME to category.mainName))
        }
    }

    override suspend fun addUserName(uid: String, name: String) {
        withContext(Dispatchers.IO) {
            rootUserDbRef.child(uid).child("name").setValue(name)
        }
    }

    override suspend fun removeCategory(category: Category) {
        withContext(Dispatchers.IO) {
            categoriesDbRef.child(category.id).removeValue()
        }
    }

    override suspend fun addCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val ref = categoriesDbRef.push()
            ref.setValue(category.copy(id = ref.key ?: "category"))
        }
    }

    private fun addWithIdCategory(category: Category) {
        val ref = categoriesDbRef.child(category.id)
        ref.setValue(category)
    }

    override suspend fun addWords(category: Category) {
        withContext(Dispatchers.IO) {
            addWithIdCategory(category.copy(words = emptyList()))
            category.words.forEach {
                addWord(it)
            }
        }
    }

    override suspend fun deleteWord(categoryId: String, wordId: String) {
        withContext(dispatcher) {
            categoriesDbRef.child(categoryId).child(WORDS_REF).child(wordId).removeValue()
        }
    }

    override suspend fun addWord(word: Word) {
        withContext(dispatcher) {
            val ref = categoriesDbRef.child(word.categoryID).child(WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: "word"))
        }
    }

    override suspend fun updateWordLearnType(word: Word) {
        withContext(Dispatchers.IO) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWord(word: Word) {
        withContext(Dispatchers.IO) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWordRepeatType(word: Word) {
        withContext(Dispatchers.IO) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(
                    mapOf(
                        Word.LEARN_OR_KNOWN to word.learnOrKnown,
                        Word.REPEAT_COUNT to word.repeatCount,
                        Word.NEXT_REPEAT_TIME to word.nextRepeatTime
                    )
                )
        }
    }

    override suspend fun updateUserTranslateCount(translateCounts: Long) {
        withContext(Dispatchers.IO) {
            userDbRef.updateChildren(mapOf(CAN_TRANSLATE_TIME_EVERY_DAY to translateCounts))
        }
    }

    override suspend fun setSelectedLanguages(vararg languageCodes: String) {
        withContext(Dispatchers.IO) {
            userDbRef.child(SELECTED_LANGUAGES).setValue(languageCodes.joinToString(","))
        }
    }

    override suspend fun updateSelectedLanguages(vararg languageCodes: String) {
        withContext(Dispatchers.IO) {
            userDbRef.updateChildren(mapOf(SELECTED_LANGUAGES to languageCodes.joinToString(",")))
        }
    }

    override fun getExploreReference() = this.exploreDbRef

    override fun getUserReference() = this.userDbRef

    override fun getApiReference() = this.apiKeys

    override fun getCategoriesReference() = this.categoriesDbRef
}