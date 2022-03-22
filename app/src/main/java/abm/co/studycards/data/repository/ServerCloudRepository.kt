package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word


interface ServerCloudRepository {
    fun updateCategoryName(category: Category)
    fun addCategory(category: Category)
    suspend fun deleteWord(categoryId: String, wordId: String)
    suspend fun addWord(word: Word)
    fun updateWordLearnType(word: Word)
    fun updateWord(word: Word)
}