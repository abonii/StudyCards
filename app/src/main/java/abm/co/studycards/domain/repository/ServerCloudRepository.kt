package abm.co.studycards.domain.repository

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.Config
import abm.co.studycards.domain.model.UserInfo
import abm.co.studycards.domain.model.Word
import abm.co.studycards.ui.explore.ParentExploreUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


interface ServerCloudRepository {
    suspend fun updateCategoryName(category: Category)
    suspend fun addCategory(category: Category)
    suspend fun addWords(category: Category)
    suspend fun addWord(word: Word)
    suspend fun updateWordLearnType(word: Word)
    suspend fun updateWord(word: Word)
    suspend fun updateWordRepeatType(word: Word)
    fun getCurrentUser(): FirebaseUser?
    suspend fun updateUserTranslateCount(translateCounts: Long)
    fun getApiReference(): DatabaseReference
    suspend fun addUserName(name: String)
    suspend fun removeCategory(category: Category)
    suspend fun setSelectedLanguages(vararg languageCodes: String)
    suspend fun updateSelectedLanguages(vararg languageCodes: String)
    fun getFirebaseAuth(): FirebaseAuth
    suspend fun addExploreCategory(setId: String, category: Category)
    fun fetchExploreSets(): StateFlow<List<ParentExploreUI>>
    fun fetchUserCategories(): StateFlow<List<Category>>
    fun fetchUserWords(): StateFlow<List<Word>>
    suspend fun removeWord(word: Word)
    suspend fun addWithIdCategory(category: Category)
    fun updateUserInfo()
    suspend fun getExploreCategory(setId: String, categoryId: String): Triple<SharedFlow<Category?>, DatabaseReference, ValueEventListener>
    fun fetchUserInfo(): StateFlow<UserInfo>
    fun removeUser()
    val config: Config
    suspend fun getTheCategory(categoryId: String): Triple<SharedFlow<Category?>, DatabaseReference, ValueEventListener>
    fun getCategoriesReference(): DatabaseReference
}