package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.network.oxford.OxfordApiServiceHelper
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.network.yandex.YandexApiServiceHelper
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import abm.co.studycards.util.Constants.ADD_CATEGORY_REF
import abm.co.studycards.util.Constants.ADD_WORD_REF
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.WORDS_REF
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VocabularyRepository @Inject constructor(
    @OxfordNetwork(TypeEnum.APIHELPER)
    private val oxfordApiHelper: OxfordApiServiceHelper,
    @YandexNetwork(TypeEnum.APIHELPER)
    private val yandexApiServiceHelper: YandexApiServiceHelper,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(WORDS_REF) private val wordsDbRef: DatabaseReference,
    @Named(ADD_CATEGORY_REF) private val addCategoryRef: DatabaseReference,
    @Named(ADD_WORD_REF) private val addWordRef: DatabaseReference,
) {
    suspend fun getOxfordWord(word: String,sl:String, tl:String) = oxfordApiHelper.getWordTranslations(word,sl,tl)

    suspend fun getYandexWord(word: String,sl:String, tl:String) = yandexApiServiceHelper.getWordTranslations(word,sl,tl)

    suspend fun getCategories() = safeApiCall(Dispatchers.IO){
        categoriesDbRef.get().await().children.mapNotNull { doc ->
            doc.getValue(Category::class.java)
        }
    }

    suspend fun getWordsByCategory(categoryId: String) = safeApiCall(Dispatchers.IO){
        wordsDbRef.child(categoryId).child(WORDS_REF).get().await().children.mapNotNull { doc ->
            doc.getValue(Category::class.java)
        }
    }

    suspend fun addCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val ref = addCategoryRef.push()
            ref.setValue(category.copy(id = ref.key ?: ""))
        }
    }

    suspend fun addWord(word: Word) {
        withContext(Dispatchers.IO) {
            val ref = addWordRef.child(word.categoryID).child(WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: ""))
        }
    }
}