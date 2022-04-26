package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImp @Inject constructor(
    @Named(Constants.CATEGORIES_REF)
    private val categoriesDbRef: DatabaseReference,
    @Named(Constants.USERS_REF)
    var userRef: DatabaseReference,
    override val firebaseAuth: FirebaseAuth
) : ServerCloudRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getCurrentUser() = firebaseAuth.currentUser

    override fun updateCategoryName(category: Category) {
        categoriesDbRef.child(category.id)
            .updateChildren(mapOf(Category.MAIN_NAME to category.mainName))
    }

    override fun addUserName(name: String) {
        userRef.child("name").setValue(name)
    }

    override fun addCategory(category: Category) {
        val ref = categoriesDbRef.push()
        ref.setValue(category.copy(id = ref.key ?: "category"))
    }

    override fun addWithIdCategory(category: Category) {
        val ref = categoriesDbRef.child(category.id)
        ref.setValue(category)
    }

    override suspend fun addWords(category: Category) {
        addWithIdCategory(category.copy(words = emptyList()))
        category.words.forEach {
            addWord(it)
        }
    }

    override suspend fun deleteWord(categoryId: String, wordId: String) {
        withContext(dispatcher) {
            categoriesDbRef.child(categoryId).child(Constants.WORDS_REF).child(wordId).removeValue()
        }
    }

    override suspend fun addWord(word: Word) {
        withContext(dispatcher) {
            val ref = categoriesDbRef.child(word.categoryID).child(Constants.WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: "word"))
        }
    }

    override fun updateWordLearnType(word: Word) {
        categoriesDbRef.child(word.categoryID)
            .child(Constants.WORDS_REF)
            .child(word.wordId)
            .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
    }

    override fun updateWord(word: Word) {
        categoriesDbRef.child(word.categoryID)
            .child(Constants.WORDS_REF)
            .child(word.wordId)
            .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
    }

    override fun updateWordRepeatType(word: Word) {
        categoriesDbRef.child(word.categoryID)
            .child(Constants.WORDS_REF)
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