package abm.co.studycards.data.repository

import abm.co.studycards.data.model.ConfigDto
import abm.co.studycards.data.model.StudyCardsMapper
import abm.co.studycards.data.model.UserInfoDto
import abm.co.studycards.data.model.UserInfoDto.Companion.CAN_TRANSLATE
import abm.co.studycards.data.model.UserInfoDto.Companion.SELECTED_LANGUAGES
import abm.co.studycards.data.model.UserInfoDto.Companion.SELECTED_LANGUAGES_SPLITTER
import abm.co.studycards.data.model.UserInfoDto.Companion.TRANSLATE_COUNT_UPDATE_TIME
import abm.co.studycards.data.model.vocabulary.CategoryDto
import abm.co.studycards.data.model.vocabulary.WordDto
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.LEARN_OR_KNOWN
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.NEXT_REPEAT_TIME
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.REPEAT_COUNT
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.Config
import abm.co.studycards.domain.model.UserInfo
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.ui.explore.ChildExploreVHUI
import abm.co.studycards.ui.explore.ParentExploreUI
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.CONFIG_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.NAME_REF
import abm.co.studycards.util.Constants.TAG
import abm.co.studycards.util.Constants.USER_REF
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.firebaseError
import abm.co.studycards.util.toDay
import abm.co.studycards.util.toStartOfTheDay
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @Named(EXPLORE_REF) private val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(USER_REF) private var userDbRef: DatabaseReference,
    @Named(CONFIG_REF) private var apiKeys: DatabaseReference,
    private var _firebaseAuth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
    private val mapper: StudyCardsMapper
) : ServerCloudRepository {

    private val _error = MutableSharedFlow<Int>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val error = _error.asSharedFlow()

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val exploreSetsStateFlow = MutableStateFlow<List<ParentExploreUI>>(emptyList())

    private val userCategoryStateFlow = MutableStateFlow<List<Category>>(emptyList())

    private val userWordsStateFlow = MutableStateFlow<List<Word>>(emptyList())

    private val userInfoStateFlow = MutableStateFlow(
        UserInfo("", 0, 0, "", "")
    )

    private var _config = Config("", "", "", 0, 0)
    override val config get() = _config

    override fun getCurrentUser() = _firebaseAuth.currentUser

    override fun fetchUserInfo(): StateFlow<UserInfo> {
        if (userInfoStateFlow.value.translateCountsUpdateTime > 0)
            return userInfoStateFlow.asStateFlow()
        userDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<UserInfoDto>()
                    ?.let { mapper.mapUserInfoDtoToModel(it) }?.let {
                        userInfoStateFlow.value = it
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }
        })
        return userInfoStateFlow.asStateFlow()
    }

    override fun updateUserInfo() {
        coroutineScope.launch {
            fetchConfig()
        }
    }

    private fun updateUserInfoIfPossible() {
        userDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = userDbRef.child(CAN_TRANSLATE)
                val time = userDbRef.child(TRANSLATE_COUNT_UPDATE_TIME)
                val startOfToday = Calendar.getInstance().toStartOfTheDay()
                val yesterdayCalendar = Calendar.getInstance().toDay(-1)
                if (getCurrentUser()?.isAnonymous == false
                    && !snapshot.child("email").exists()
                ) {
                    userDbRef.child("email")
                        .setValue(getCurrentUser()?.email)
                }
                if (!snapshot.child(CAN_TRANSLATE).exists()) {
                    when (getCurrentUser()?.isAnonymous) {
                        true -> {
                            count.setValue(_config.translateCountAnonymous)
                        }
                        else -> {
                            count.setValue(_config.translateCount)
                        }
                    }
                }
                if (!snapshot.child(TRANSLATE_COUNT_UPDATE_TIME).exists()) {
                    time.setValue(startOfToday.timeInMillis)
                } else if (snapshot.child(TRANSLATE_COUNT_UPDATE_TIME).value != null
                    && (snapshot.value as Map<*, *>)[TRANSLATE_COUNT_UPDATE_TIME] as Long
                    <= yesterdayCalendar.timeInMillis
                ) {
                    userDbRef.updateChildren(mapOf(TRANSLATE_COUNT_UPDATE_TIME to startOfToday.timeInMillis))
                    val currentTimes =
                        snapshot.child(CAN_TRANSLATE).value as Long
                    userDbRef.updateChildren(
                        mapOf(
                            CAN_TRANSLATE to when {
                                currentTimes > _config.translateCount -> {
                                    currentTimes + Constants.ADJUST_DAY_BOUGHT_USER
                                }
                                getCurrentUser()?.isAnonymous == true -> {
                                    _config.translateCountAnonymous
                                }
                                else -> {
                                    _config.translateCount
                                }
                            }
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }
        })
    }

    private fun fetchConfig() {
        apiKeys.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<ConfigDto>()?.let {
                    _config = mapper.mapConfigDtoToModel(it)
                }
                Log.i(TAG, "onDataChange: $config")
                updateUserInfoIfPossible()
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }
        })
    }

    override fun fetchExploreSets(): StateFlow<List<ParentExploreUI>> {
        if (exploreSetsStateFlow.value.isNotEmpty())
            return exploreSetsStateFlow.asStateFlow()
        exploreDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.Default) {
                    val items = mutableListOf<ParentExploreUI>()
                    snapshot.children.forEach { set ->
                        val sets1 = mutableListOf<Category>()
                        val setName = set.child(NAME_REF).getValue<String>().toString()
                        val setId = set.key.toString()
                        set.child(CATEGORIES_REF).children.forEach {
                            try {
                                it.getValue(CategoryDto::class.java)?.let { it1 ->
                                    sets1.add(
                                        mapper.mapCategoryDtoToModel(it1).copy(words = emptyList())
                                    )
                                }
                            } catch (e: DatabaseException) {
                                Log.e(TAG, "fetchExploreSets: ${e.message}")
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

    override fun fetchUserCategories(): StateFlow<List<Category>> {
        if (userCategoryStateFlow.value.isNotEmpty())
            return userCategoryStateFlow.asStateFlow()
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.Default) {
                    val items = mutableListOf<Category>()
                    snapshot.children.forEach {
                        try {
                            it.getValue(CategoryDto::class.java)
                                ?.let { it1 -> items.add(mapper.mapCategoryDtoToModel(it1)) }
                        } catch (e: DatabaseException) {
                            Log.e(TAG, "fetchUserCategory: ${e.message}")
                        }
                    }
                    userCategoryStateFlow.value = items.take(500)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }
        })
        return userCategoryStateFlow.asStateFlow()
    }

    override fun fetchUserWords(): StateFlow<List<Word>> {
        if (userWordsStateFlow.value.isNotEmpty())
            return userWordsStateFlow.asStateFlow()
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.Default) {
                    val items = mutableListOf<Word>()
                    snapshot.children.forEach { category ->
                        category.child(WORDS_REF).children.forEach {
                            try {
                                it.getValue(WordDto::class.java)?.let { word ->
                                    items.add(mapper.mapWordDtoToModel(word))
                                }
                            } catch (e: DatabaseException) {
                                Log.e(TAG, "fetchUserWords: ${e.message}")
                            }
                        }
                    }
                    userWordsStateFlow.value = items
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.tryEmit(firebaseError(error.code))
            }

        })
        return userWordsStateFlow.asStateFlow()
    }

    override suspend fun getTheCategory(categoryId: String):
            Triple<SharedFlow<Category?>, DatabaseReference, ValueEventListener> {
        val categorySharedFlow = MutableSharedFlow<Category?>()
        val ref = categoriesDbRef.child(categoryId)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val cat = snapshot.getValue(CategoryDto::class.java)?.let {
                        mapper.mapCategoryDtoToModel(it)
                    }
                    Log.i(TAG, "getTheCategory: ${cat?.name}")
                    categorySharedFlow.emit(cat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                categorySharedFlow.tryEmit(null)
            }
        })

        return Triple(categorySharedFlow.asSharedFlow(), ref, listener)
    }

    override suspend fun getExploreCategory(setId: String, categoryId: String):
            Triple<SharedFlow<Category?>, DatabaseReference, ValueEventListener> {
        val categorySharedFlow = MutableSharedFlow<Category?>()
        val ref = exploreDbRef.child(setId).child(CATEGORIES_REF).child(categoryId)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val cat = snapshot.getValue(CategoryDto::class.java)?.let {
                        mapper.mapCategoryDtoToModel(it)
                    }
                    Log.i(TAG, "getExploreCategory")
                    categorySharedFlow.emit(cat)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return Triple(categorySharedFlow, ref, listener)
    }


    override suspend fun updateCategoryName(category: Category) {
        withContext(dispatcher) {
            categoriesDbRef.child(category.id)
                .updateChildren(mapOf(Category.NAME to category.name))
        }
    }

    override suspend fun addUserName(name: String) {
        withContext(dispatcher) {
            userDbRef.child(NAME_REF).setValue(name)
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
            ref.setValue(
                mapper.mapCategoryModelToDto(
                    category.copy(
                        id = ref.key ?: "category"
                    )
                )
            )
        }
    }

    override suspend fun addExploreCategory(setId: String, category: Category) {
        withContext(Dispatchers.IO) {
            val ref = exploreDbRef.child(setId).child(CATEGORIES_REF)
            ref.child(category.id).setValue(mapper.mapCategoryModelToDto(category))
        }
    }

    override suspend fun addWithIdCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val ref = categoriesDbRef.child(category.id)
            val categoryDto = mapper.mapCategoryModelToDto(category)
            ref.setValue(categoryDto)
        }
    }

    override suspend fun addWords(category: Category) {
        withContext(dispatcher) {
            addWithIdCategory(category.copy(words = ArrayList()))
            category.words.forEach {
                addWord(it)
            }
        }
    }

    override suspend fun removeWord(word: Word) {
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID).child(WORDS_REF).child(word.wordId)
                .removeValue()
        }
    }

    override suspend fun addWord(word: Word) {
        withContext(dispatcher) {
            val ref = categoriesDbRef.child(word.categoryID).child(WORDS_REF).push()
            ref.setValue(mapper.mapModelToWordDto(word.copy(wordId = ref.key ?: "word")))
        }
    }

    override suspend fun updateWordLearnType(word: Word) {
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWord(word: Word) {
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }

    override suspend fun updateWordRepeatType(word: Word) {
        withContext(dispatcher) {
            categoriesDbRef.child(word.categoryID)
                .child(WORDS_REF)
                .child(word.wordId)
                .updateChildren(
                    mapOf(
                        LEARN_OR_KNOWN to word.learnOrKnown,
                        REPEAT_COUNT to word.repeatCount,
                        NEXT_REPEAT_TIME to word.nextRepeatTime
                    )
                )
        }
    }

    override suspend fun updateUserTranslateCount(translateCounts: Long) {
        withContext(dispatcher) {
            userDbRef.updateChildren(mapOf(CAN_TRANSLATE to translateCounts))
        }
    }

    override suspend fun setSelectedLanguages(vararg languageCodes: String) {
        withContext(dispatcher) {
            userDbRef.child(SELECTED_LANGUAGES)
                .setValue(languageCodes.joinToString(SELECTED_LANGUAGES_SPLITTER))
        }
    }

    override suspend fun updateSelectedLanguages(vararg languageCodes: String) {
        withContext(dispatcher) {
            userDbRef.updateChildren(
                mapOf(
                    SELECTED_LANGUAGES to languageCodes.joinToString(
                        SELECTED_LANGUAGES_SPLITTER
                    )
                )
            )
        }
    }

    override fun removeUser() {
        userDbRef.removeValue()
    }

    override fun getApiReference() = this.apiKeys

    override fun getCategoriesReference() = this.categoriesDbRef

    override fun getFirebaseAuth() = this._firebaseAuth
}