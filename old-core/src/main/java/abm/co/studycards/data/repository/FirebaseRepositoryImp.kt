package abm.co.studycards.data.repository

import abm.co.studycards.R
import abm.co.studycards.data.model.ConfigDto
import abm.co.studycards.data.model.ParentSetDto
import abm.co.studycards.data.model.StudyCardsMapper
import abm.co.studycards.data.model.UserInfoDto
import abm.co.studycards.data.model.UserInfoDto.Companion.CAN_TRANSLATE
import abm.co.studycards.data.model.UserInfoDto.Companion.EMAIL
import abm.co.studycards.data.model.UserInfoDto.Companion.SELECTED_LANGUAGES
import abm.co.studycards.data.model.UserInfoDto.Companion.SELECTED_LANGUAGES_SPLITTER
import abm.co.studycards.data.model.UserInfoDto.Companion.TRANSLATE_COUNT_UPDATE_TIME
import abm.co.studycards.data.model.vocabulary.CategoryDto
import abm.co.studycards.data.model.vocabulary.WordDto
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.LEARN_OR_KNOWN
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.NEXT_REPEAT_TIME
import abm.co.studycards.data.model.vocabulary.WordDto.Companion.REPEAT_COUNT
import abm.co.studycards.domain.model.*
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.CONFIG_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.NAME_REF
import abm.co.studycards.util.Constants.TAG
import abm.co.studycards.util.Constants.TAG_ERROR
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
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @Named(EXPLORE_REF) private val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(USER_REF) private var userDbRef: DatabaseReference,
    @Named(CONFIG_REF) private var configKey: DatabaseReference,
    private var _firebaseAuth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
    private val mapper: StudyCardsMapper
) : ServerCloudRepository {

//    private val _error = MutableSharedFlow<Int>(
//        extraBufferCapacity = 1,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
//    )
//    val error = _error.asSharedFlow()

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private var _config = Config("", "", "", 0, 0)
    override val config get() = _config

    private val exploreSetsStateFlow =
        MutableStateFlow<ResultWrapper<List<ParentSet>>>(ResultWrapper.Loading)

    override fun fetchExploreSets(): StateFlow<ResultWrapper<List<ParentSet>>> {
        if (exploreSetsStateFlow.value !is ResultWrapper.Loading)
            return exploreSetsStateFlow.asStateFlow()
        exploreDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i(TAG, "impl fetchExploreSets")
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val items = ArrayList<ParentSet>()
                        snapshot.children.forEach { set ->
                            set.getValue(ParentSetDto::class.java)
                                ?.let { items.add(mapper.mapSetDtoToMode(it)) }
                        }
                        if (items.isEmpty()) {
                            exploreSetsStateFlow.value =
                                ResultWrapper.Error(res = R.string.empty_in_explore)
                        } else exploreSetsStateFlow.value = ResultWrapper.Success(items)

                    } catch (e: DatabaseException) {
                        exploreSetsStateFlow.value = ResultWrapper.Error()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                exploreSetsStateFlow.value =
                    ResultWrapper.Error(res = firebaseError(error.code))
            }
        })
        return exploreSetsStateFlow.asStateFlow()
    }

    private val userCategoryStateFlow =
        MutableStateFlow<ResultWrapper<List<Category>>>(ResultWrapper.Loading)

    override fun fetchUserCategories(): StateFlow<ResultWrapper<List<Category>>> {
        if (userCategoryStateFlow.value !is ResultWrapper.Loading)
            return userCategoryStateFlow.asStateFlow()
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    val items = mutableListOf<Category>()
                    try {
                        snapshot.children.forEach {
                            it.getValue(CategoryDto::class.java)
                                ?.let { it1 -> items.add(mapper.mapCategoryDtoToModel(it1)) }
                        }
                    } catch (e: DatabaseException) {
                        Log.e(TAG_ERROR, "fetchUserCategories: ${e.message}")
                    }
//                    Log.i(TAG, "impl fetchUserCategories: ${items.size}")
                    if (items.isEmpty()) {
                        userCategoryStateFlow.value = ResultWrapper.Error(res = R.string.empty)
                    } else userCategoryStateFlow.value = ResultWrapper.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userCategoryStateFlow.value =
                    ResultWrapper.Error(res = firebaseError(error.code))
            }
        })
        return userCategoryStateFlow.asStateFlow()
    }

    private val userWordsStateFlow =
        MutableStateFlow<ResultWrapper<List<Word>>>(ResultWrapper.Loading)

    override fun fetchUserWords(): StateFlow<ResultWrapper<List<Word>>> {
        if (userWordsStateFlow.value !is ResultWrapper.Loading)
            return userWordsStateFlow.asStateFlow()
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    val items = mutableListOf<Word>()
                    snapshot.children.forEach { category ->
                        try {
                            category.child(WORDS_REF).children.forEach {
                                it.getValue(WordDto::class.java)?.let { word ->
                                    items.add(mapper.mapWordDtoToModel(word))
                                }
                            }
                        } catch (e: DatabaseException) {
                            Log.e(TAG_ERROR, "fetchUserWords: ${e.message}")
                        }
                    }
                    Log.i(TAG, "impl fetchUserWords: $items")
                    if (items.isEmpty()) {
                        userWordsStateFlow.value =
                            ResultWrapper.Error(res = R.string.empty_in_vocabulary)
                    } else userWordsStateFlow.value = ResultWrapper.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userWordsStateFlow.value = ResultWrapper.Error(res = firebaseError(error.code))
            }

        })
        return userWordsStateFlow.asStateFlow()
    }

    override fun getTheCategory(categoryId: String):
            Triple<SharedFlow<ResultWrapper<Category?>>, DatabaseReference, ValueEventListener> {
        val categorySharedFlow = MutableSharedFlow<ResultWrapper<Category?>>()
        val ref = categoriesDbRef.child(categoryId)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i(TAG, "impl getTheCategory")
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val catDto = snapshot.getValue(CategoryDto::class.java)
                        val cat = catDto?.let { mapper.mapCategoryDtoToModel(it) }
                        categorySharedFlow.emit(ResultWrapper.Success(cat))

                    } catch (e: Exception) {
                        Log.e(TAG_ERROR, "onDataChange: " + e.message)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                categorySharedFlow.tryEmit(ResultWrapper.Error(res = firebaseError(error.code)))
            }
        })

        return Triple(categorySharedFlow.asSharedFlow(), ref, listener)
    }

    override fun getExploreCategory(setId: String, categoryId: String):
            Triple<SharedFlow<ResultWrapper<Category>>, DatabaseReference, ValueEventListener> {
        val categorySharedFlow = MutableSharedFlow<ResultWrapper<Category>>()
        val ref = exploreDbRef.child(setId).child(CATEGORIES_REF).child(categoryId)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    val cat = snapshot.getValue(CategoryDto::class.java)?.let {
                        mapper.mapCategoryDtoToModel(it)
                    }
                    if (cat != null) {
                        categorySharedFlow.emit(ResultWrapper.Success(cat))
                    } else {
                        categorySharedFlow.emit(ResultWrapper.Error())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                categorySharedFlow.tryEmit(ResultWrapper.Error(res = firebaseError(error.code)))
            }
        })
        return Triple(categorySharedFlow.asSharedFlow(), ref, listener)
    }

    private val userInfoStateFlow = MutableStateFlow(UserInfo("", 0, 0, "", emptyList()))
    override fun fetchUserInfo(): StateFlow<UserInfo> {
        if (userInfoStateFlow.value.translateCountsUpdateTime > 0)
            return userInfoStateFlow.asStateFlow()
        userDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i(TAG, "impl fetchUserInfo")
                snapshot.getValue<UserInfoDto>()
                    ?.let { mapper.mapUserInfoDtoToModel(it) }?.let {
                        userInfoStateFlow.value = it
                    }
            }

            override fun onCancelled(error: DatabaseError) {
//                _error.tryEmit(firebaseError(error.code))
                Log.e(TAG_ERROR, "onCancelled: ${error.message}")
            }
        })
        return userInfoStateFlow.asStateFlow()
    }

    override fun getCurrentUser() = _firebaseAuth.currentUser

    override fun updateUserInfo() {
        configKey.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<ConfigDto>()?.let {
                    _config = mapper.mapConfigDtoToModel(it)
                }
                updateUserInfoIfPossible()
            }

            override fun onCancelled(error: DatabaseError) {
//                _error.tryEmit(firebaseError(error.code))
                Log.e(TAG_ERROR, "onCancelled: ${error.message}")
            }
        })
    }

    private fun updateUserInfoIfPossible() {
        userDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    setEmailIfNotExist(snapshot)
                    setTranslateCountIfNotExist(snapshot)
                    checkAndUpdateTranslateCountAndTime(snapshot)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                _error.tryEmit(firebaseError(error.code))
                Log.e(TAG_ERROR, "onCancelled: ${error.message}")
            }
        })
    }

    private fun checkAndUpdateTranslateCountAndTime(snapshot: DataSnapshot) {
        val time = userDbRef.child(TRANSLATE_COUNT_UPDATE_TIME)
        val startOfToday = Calendar.getInstance().toStartOfTheDay()
        val yesterdayCalendar = Calendar.getInstance().toDay(-1)
        if (!snapshot.child(TRANSLATE_COUNT_UPDATE_TIME).exists()) {
            time.setValue(startOfToday.timeInMillis)
        } else if (snapshot.child(TRANSLATE_COUNT_UPDATE_TIME).value != null
            && (snapshot.value as Map<*, *>)[TRANSLATE_COUNT_UPDATE_TIME] as Long
            <= yesterdayCalendar.timeInMillis
        ) {
            val currentTranslateCounts =
                snapshot.child(CAN_TRANSLATE).value as Long
            updateTranslateCountAndTime(currentTranslateCounts, startOfToday.timeInMillis)
        }
    }

    private fun updateTranslateCountAndTime(
        currentTranslateCounts: Long, updatedTime: Long
    ) {
        userDbRef.updateChildren(mapOf(TRANSLATE_COUNT_UPDATE_TIME to updatedTime))
        userDbRef.updateChildren(
            mapOf(
                CAN_TRANSLATE to when {
                    currentTranslateCounts > config.translateCount -> {
                        currentTranslateCounts + Constants.ADJUST_DAY_BOUGHT_USER
                    }
                    getCurrentUser()?.isAnonymous == true ->
                        config.translateCountAnonymous

                    else -> config.translateCount
                }
            )
        )
    }

    private fun setTranslateCountIfNotExist(snapshot: DataSnapshot) {
        val count = userDbRef.child(CAN_TRANSLATE)
        if (!snapshot.child(CAN_TRANSLATE).exists()) {
            when (getCurrentUser()?.isAnonymous) {
                true -> {
                    count.setValue(config.translateCountAnonymous)
                }
                else -> {
                    count.setValue(config.translateCount)
                }
            }
        }
    }

    private fun setEmailIfNotExist(snapshot: DataSnapshot) {
        if (getCurrentUser()?.isAnonymous == false
            && !snapshot.child(EMAIL).exists()
        ) {
            userDbRef.child(EMAIL).setValue(getCurrentUser()?.email)
        }
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

    override suspend fun deleteCategory(category: Category) {
        withContext(dispatcher) {
            categoriesDbRef.child(category.id).removeValue()
        }
    }

    override suspend fun deleteExploreCategory(setId: String, category: Category) {
        withContext(dispatcher) {
            exploreDbRef.child(setId).child(CATEGORIES_REF).child(category.id).removeValue()
            delay(50)
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
            ref.child(category.id).setValue(
                mapper.mapCategoryModelToDto(
                    category.copy(
                        creatorId = getCurrentUser()?.uid ?: "no-uid",
                        creatorName = userInfoStateFlow.value.name
                    )
                )
            )
        }
    }

    override suspend fun addWithIdCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val ref = categoriesDbRef.child(category.id)
            val categoryDto = mapper.mapCategoryModelToDto(category)
            ref.setValue(categoryDto)
        }
    }

    override suspend fun deleteWord(word: Word) {
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
                .updateChildren(mapOf(word.wordId to mapper.mapModelToWordDto(word)))
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

    override suspend fun addSelectedLanguages(vararg languageCodes: String) {
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

    override fun deleteUser() {
        userDbRef.removeValue()
    }

    override fun getFirebaseAuth() = this._firebaseAuth
}