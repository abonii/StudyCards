package abm.co.studycards.data.repository

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.CategoryDto
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.ui.explore.ChildExploreVHUI
import abm.co.studycards.ui.explore.ParentExploreUI
import abm.co.studycards.util.Constants.API_REF
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_EVERY_DAY
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.NAME_REF
import abm.co.studycards.util.Constants.SELECTED_LANGUAGES
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.Constants.USER_REF
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.firebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @Named(EXPLORE_REF) private val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(USERS_REF) private var userDbRef: DatabaseReference,
    @Named(USER_REF) private var rootUserDbRef: DatabaseReference,
    @Named(API_REF) private var apiKeys: DatabaseReference,
    private var _firebaseAuth: FirebaseAuth,
    private val coroutineScope: CoroutineScope
) : ServerCloudRepository {

    private val _error = MutableSharedFlow<Int>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val error = _error.asSharedFlow()

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getCurrentUser() = _firebaseAuth.currentUser

    private val exploreSetsStateFlow = MutableStateFlow<List<ParentExploreUI>>(emptyList())

    override fun fetchExploreSets(): StateFlow<List<ParentExploreUI>> {
        if (exploreSetsStateFlow.value.isNotEmpty())
            return exploreSetsStateFlow
        exploreDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(dispatcher) {
                    val items = mutableListOf<ParentExploreUI>()
                    snapshot.children.forEach { set ->
                        val sets1 = mutableListOf<Category>()
                        val setName = set.child(NAME_REF).getValue<String>().toString()
                        val setId = set.key.toString()
                        set.child(CATEGORIES_REF).children.forEach {
                            it.getValue(CategoryDto::class.java)?.let { it1 ->
                                sets1.add(it1.toCategory())
                            }
                        }
                        items.add(
                            ParentExploreUI.SetUI(
                                sets1.map {
                                    ChildExploreVHUI.VHCategory(it)
                                }, setName, setId
                            )
                        )
                    }
                    exploreSetsStateFlow.value = items
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }
        })
        return exploreSetsStateFlow.asStateFlow()
    }

    override suspend fun updateCategoryName(category: Category) {
        withContext(dispatcher) {
            categoriesDbRef.child(category.id)
                .updateChildren(mapOf(Category.MAIN_NAME to category.mainName))
        }
    }

    override suspend fun addUserName(uid: String, name: String) {
        withContext(dispatcher) {
            rootUserDbRef.child(uid).child(NAME_REF).setValue(name)
        }
    }

    override suspend fun removeCategory(category: Category) {
        withContext(dispatcher) {
            categoriesDbRef.child(category.id).removeValue()
        }
    }

    override suspend fun addCategory(category: Category) {
        withContext(dispatcher) {
            val ref = categoriesDbRef.push()
            ref.setValue(category.copy(id = ref.key ?: "category"))
        }
    }

    override fun addExploreCategory(setId: String, category: Category) {
        val ref = exploreDbRef.child(setId).child(CATEGORIES_REF)
        ref.child(category.id).setValue(category)
    }

    private fun addWithIdCategory(category: Category) {
        val ref = categoriesDbRef.child(category.id)
        ref.setValue(category)
    }

    override suspend fun addWords(category: Category) {
        withContext(dispatcher) {
            addWithIdCategory(category.copy(words = ArrayList()))
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
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWord(word: Word) {
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWordRepeatType(word: Word) {
        withContext(dispatcher) {
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
        withContext(dispatcher) {
            userDbRef.updateChildren(mapOf(CAN_TRANSLATE_TIME_EVERY_DAY to translateCounts))
        }
    }

    override suspend fun setSelectedLanguages(vararg languageCodes: String) {
        withContext(dispatcher) {
            userDbRef.child(SELECTED_LANGUAGES).setValue(languageCodes.joinToString(","))
        }
    }

    override suspend fun updateSelectedLanguages(vararg languageCodes: String) {
        withContext(dispatcher) {
            userDbRef.updateChildren(mapOf(SELECTED_LANGUAGES to languageCodes.joinToString(",")))
        }
    }

    override fun getExploreReference() = this.exploreDbRef

    override fun getUserReference() = this.userDbRef

    override fun getApiReference() = this.apiKeys

    override fun getCategoriesReference() = this.categoriesDbRef

    override fun getFirebaseAuth() = this._firebaseAuth
}