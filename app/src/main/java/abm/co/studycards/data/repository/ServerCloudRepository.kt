package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.ui.explore.ParentExploreUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.StateFlow


interface ServerCloudRepository {
    suspend fun updateCategoryName(category: Category)
    suspend fun addCategory(category: Category)
    suspend fun addWords(category: Category)
    suspend fun deleteWord(categoryId: String, wordId: String)
    suspend fun addWord(word: Word)
    suspend fun updateWordLearnType(word: Word)
    suspend fun updateWord(word: Word)
    suspend fun updateWordRepeatType(word: Word)
    fun getCurrentUser(): FirebaseUser?
    suspend fun updateUserTranslateCount(translateCounts: Long)
    fun getUserReference(): DatabaseReference
    fun getApiReference(): DatabaseReference
    fun getCategoriesReference(): DatabaseReference
    fun getExploreReference(): DatabaseReference
    suspend fun addUserName(uid: String, name: String)
    suspend fun removeCategory(category: Category)
    suspend fun setSelectedLanguages(vararg languageCodes: String)
    suspend fun updateSelectedLanguages(vararg languageCodes: String)
    fun getFirebaseAuth(): FirebaseAuth
    fun addExploreCategory(setId: String, category: Category)
    fun fetchExploreSets(): StateFlow<List<ParentExploreUI>>
}