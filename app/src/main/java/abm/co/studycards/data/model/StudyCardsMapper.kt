package abm.co.studycards.data.model

import abm.co.studycards.data.model.oxford.EntryDto
import abm.co.studycards.data.model.oxford.ResultsEntryDto
import abm.co.studycards.data.model.oxford.RetrieveEntryDto
import abm.co.studycards.data.model.oxford.SenseDto
import abm.co.studycards.data.model.vocabulary.CategoryDto
import abm.co.studycards.data.model.vocabulary.WordDto
import abm.co.studycards.domain.model.*
import javax.inject.Inject

class StudyCardsMapper @Inject constructor() {

    fun mapSetDtoToMode(dto: ParentSetDto) = ParentSet(
        name = dto.name,
        id = dto.id,
        categories = ArrayList(dto.categories.values).map { mapCategoryDtoToModel(it) }
    )

    fun mapConfigDtoToModel(dto: ConfigDto) = Config(
        oxfordId = dto.oxfordId,
        yandexKey = dto.yandexKey,
        oxfordKey = dto.oxfordKey,
        translateCount = dto.translateCount,
        translateCountAnonymous = dto.translateCountAnonymous
    )

    fun mapUserInfoDtoToModel(dto: UserInfoDto) = UserInfo(
        name = dto.name,
        translateCounts = dto.translateCounts,
        translateCountsUpdateTime = dto.translateCountsUpdateTime,
        email = dto.email,
        selectedLanguages = dto.selectedLanguages
    )

    fun mapCategoryDtoToModel(dto: CategoryDto) = Category(
        dto.id, dto.name, dto.sourceLanguage, dto.targetLanguage, dto.imageUrl,
        dto.creatorName, dto.creatorId, ArrayList(dto.words.values).map { mapWordDtoToModel(it) }
    )

    fun mapCategoryModelToDto(c: Category) = CategoryDto(
        id = c.id,
        name = c.name,
        sourceLanguage = c.sourceLanguage,
        targetLanguage = c.targetLanguage,
        imageUrl = c.imageUrl,
        creatorName = c.creatorName,
        creatorId = c.creatorId,
        words = c.words.map {
            mapModelToWordDto(it)
        }.associateBy { it.wordId }.toMap()
    )

    fun mapOxfordDtoToModel(dto: RetrieveEntryDto) = OxfordResult(
        word = dto.word ?: "",
        lexicalCategories = getLexicalCategoriesFromDto(dto.results)
    )

    fun mapWordDtoToModel(dto: WordDto) = Word(
        name = dto.name,
        translations = dto.translations,
        imageUrl = dto.imageUrl,
        examples = dto.examples,
        learnOrKnown = dto.learnOrKnown,
        sourceLanguage = dto.sourceLanguage,
        targetLanguage = dto.targetLanguage,
        categoryID = dto.categoryID,
        repeatCount = dto.repeatCount,
        nextRepeatTime = dto.nextRepeatTime,
        wordId = dto.wordId
    )

    fun mapModelToWordDto(dto: Word) = WordDto(
        name = dto.name,
        translations = dto.translations,
        imageUrl = dto.imageUrl,
        examples = dto.examples,
        learnOrKnown = dto.learnOrKnown,
        sourceLanguage = dto.sourceLanguage,
        targetLanguage = dto.targetLanguage,
        categoryID = dto.categoryID,
        repeatCount = dto.repeatCount,
        nextRepeatTime = dto.nextRepeatTime,
        wordId = dto.wordId
    )

    private fun getLexicalCategoriesFromDto(resultsDto: List<ResultsEntryDto>): List<LexicalCategory> {
        val lexicalCategories = ArrayList<LexicalCategory>()
        resultsDto.forEach { results ->
            results.lexicalEntries?.forEach { lexical ->
                lexicalCategories.add(
                    LexicalCategory(
                        lexicalName = lexical.lexicalCategory?.text ?: "",
                        details = getCategoryDetailsFromLexicalCategory(lexical.entries)
                    )
                )
            }
        }
        return lexicalCategories
    }

    private fun getCategoryDetailsFromLexicalCategory(entriesDto: List<EntryDto>?): List<CategoryDetails> {
        val details = ArrayList<CategoryDetails>()
        entriesDto?.forEach { entry ->
            entry.senses?.forEach { sense ->
                details.add(
                    CategoryDetails(
                        translations = getTranslationsFromLexicalCategory(sense),
                        examples = getExamplesFromLexicalCategory(sense)
                    )
                )
            }
        }
        return details
    }

    private fun getTranslationsFromLexicalCategory(senseDto: SenseDto): List<String> {
        val translations = ArrayList<String>()
        senseDto.translations?.forEach {
            val trans = it.getNormalTranslation() ?: "***"
            if (!translations.contains(trans))
                translations.add(trans)
        }
        return translations
    }

    private fun getExamplesFromLexicalCategory(senseDto: SenseDto): List<String> {
        val examples = ArrayList<String>()
        senseDto.examples?.forEach {
            examples.add(
                "${it.text} - ${it.translations?.getOrNull(0)?.getNormalTranslation()}"
            )
        }
        return examples
    }
}