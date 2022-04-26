package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


interface ServerCloudRepository {
    val firebaseAuth:FirebaseAuth
    fun updateCategoryName(category: Category)
    fun addCategory(category: Category)
    fun addWithIdCategory(category: Category)
    suspend fun addWords(category: Category)
    suspend fun deleteWord(categoryId: String, wordId: String)
    suspend fun addWord(word: Word)
    fun updateWordLearnType(word: Word)
    fun updateWord(word: Word)
    fun updateWordRepeatType(word: Word)
    fun getCurrentUser(): FirebaseUser?
    fun addUserName(name: String)
}