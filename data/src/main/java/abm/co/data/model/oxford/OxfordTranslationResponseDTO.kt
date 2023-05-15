package abm.co.data.model.oxford

import abm.co.domain.model.oxford.OxfordTranslationResponse
import androidx.annotation.Keep

@Keep
data class OxfordTranslationResponseDTO(
    val results: List<ResultDTO>?,
    val word: String?
) {

    @Keep
    data class ResultDTO(
        val lexicalEntries: List<LexicalEntryDTO>?
    ) {

        @Keep
        data class LexicalEntryDTO(
            val entries: List<EntryDTO>?,
            val lexicalCategory: LexicalCategoryDTO?
        ) {

            @Keep
            data class EntryDTO(
                val senses: List<SenseDTO>?
            ) {

                @Keep
                data class SenseDTO(
                    val examples: List<ExampleDTO>?,
                    val translations: List<ExampleDTO.TranslationDTO>?
                ) {

                    @Keep
                    data class ExampleDTO(
                        val text: String?,
                        val translations: List<TranslationDTO>?
                    ) {
                        @Keep
                        data class TranslationDTO(
                            val text: String?
                        )
                    }
                }
            }

            @Keep
            data class LexicalCategoryDTO(
                val text: String?
            )
        }
    }
}

fun OxfordTranslationResponseDTO.toDomain() = OxfordTranslationResponse(
    lexicalEntry = results?.flatMap { result ->
        result.lexicalEntries?.map { it.toDomain() } ?: emptyList()
    },
    word = word
)

fun OxfordTranslationResponseDTO.ResultDTO.LexicalEntryDTO.EntryDTO.SenseDTO.ExampleDTO.toDomain() =
    OxfordTranslationResponse.LexicalEntry.Entry.Example(
        text = text,
        translations = translations?.mapNotNull { it.text }?.joinToString("; ")
    )

fun OxfordTranslationResponseDTO.ResultDTO.LexicalEntryDTO .toDomain(): OxfordTranslationResponse.LexicalEntry {
    val entries: ArrayList<OxfordTranslationResponse.LexicalEntry.Entry> = ArrayList()
    this.entries?.forEach {
        it.senses?.forEach { sense ->
            entries.add(
                OxfordTranslationResponse.LexicalEntry.Entry(
                    translations = sense.toTranslations(),
                    examples = sense.toExamples()
                )
            )
        }
    }
    return OxfordTranslationResponse.LexicalEntry(
        entries = entries,
        lexicalKind = lexicalCategory?.text
    )
}

fun OxfordTranslationResponseDTO.ResultDTO.LexicalEntryDTO.EntryDTO.SenseDTO.toTranslations(): List<String> {
    val translations = ArrayList<String>()
    this.translations?.forEach {
        val trans = it.text ?: "***"
        translations.add(trans)
    } ?: translations.add("--------")
    return translations
}
fun OxfordTranslationResponseDTO.ResultDTO.LexicalEntryDTO.EntryDTO.SenseDTO.toExamples(): ArrayList<OxfordTranslationResponse.LexicalEntry.Entry.Example> {
    val examples = ArrayList<OxfordTranslationResponse.LexicalEntry.Entry.Example>()
    this.examples?.forEach {
        examples.add(
            OxfordTranslationResponse.LexicalEntry.Entry.Example(
                text = it.text,
                translations = it.translations?.joinToString("; ")
            )
        )
    }
    return examples
}