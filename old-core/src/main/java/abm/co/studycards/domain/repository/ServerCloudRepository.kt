package abm.co.studycards.domain.repository

import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


interface ServerCloudRepository {
    val config: Config
    fun deleteUser()
    fun updateUserInfo()
    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth
    fun fetchUserInfo(): StateFlow<UserInfo>
    fun fetchUserWords(): StateFlow<ResultWrapper<List<Word>>>
    fun fetchExploreSets(): StateFlow<ResultWrapper<List<ParentSet>>>
    fun fetchUserCategories(): StateFlow<ResultWrapper<List<Category>>>
    fun getTheCategory(categoryId: String): Triple<SharedFlow<ResultWrapper<Category?>>, DatabaseReference, ValueEventListener>
    fun getExploreCategory(setId: String, categoryId: String): Triple<SharedFlow<ResultWrapper<Category>>, DatabaseReference, ValueEventListener>
    suspend fun addWord(word: Word)
    suspend fun addCategory(category: Category)
    suspend fun addUserName(name: String)
    suspend fun addWithIdCategory(category: Category)
    suspend fun addExploreCategory(setId: String, category: Category)
    suspend fun addSelectedLanguages(vararg languageCodes: String)
    suspend fun updateWord(word: Word)
    suspend fun updateCategoryName(category: Category)
    suspend fun updateWordLearnType(word: Word)
    suspend fun updateWordRepeatType(word: Word)
    suspend fun updateSelectedLanguages(vararg languageCodes: String)
    suspend fun updateUserTranslateCount(translateCounts: Long)
    suspend fun deleteWord(word: Word)
    suspend fun deleteCategory(category: Category)
    suspend fun deleteExploreCategory(setId: String, category: Category)
}