package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.util.Constants
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseRepository(private val categoriesDbRef: DatabaseReference) {

    fun updateCategoryName(category: Category) {
        categoriesDbRef.child(category.id)
            .updateChildren(mapOf(Category.MAIN_NAME to category.mainName))

    }

    fun addCategory(category: Category) {
        val ref = categoriesDbRef.push()
        ref.setValue(category.copy(id = ref.key ?: ""))
    }

    suspend fun deleteWord(categoryId: String, wordId: String) {
        safeApiCall(Dispatchers.IO) {
            categoriesDbRef.child(categoryId).child(Constants.WORDS_REF).child(wordId).removeValue()
        }
    }

    suspend fun addWord(word: Word) {
        withContext(Dispatchers.IO) {
            val ref = categoriesDbRef.child(word.categoryID).child(Constants.WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: "word"))
        }
    }

    fun updateWordLearnType(word: Word) {
        categoriesDbRef.child(word.categoryID)
            .child(Constants.WORDS_REF)
            .child(word.wordId)
            .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
    }
}