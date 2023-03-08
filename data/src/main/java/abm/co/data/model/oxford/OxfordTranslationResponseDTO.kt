package abm.co.data.model.oxford

import androidx.annotation.Keep

@Keep
data class OxfordTranslationResponseDTO(
    val results: List<ResultDTO>,
    val word: String
) {

    @Keep
    data class ResultDTO(
        val lexicalEntries: List<LexicalEntryDTO>,
        val word: String
    ) {

        @Keep
        data class LexicalEntryDTO(
            val entries: List<EntryDTO>,
            val language: String,
            val lexicalCategory: LexicalCategoryDTO,
            val text: String
        ) {

            @Keep
            data class EntryDTO(
                val etymologies: List<String>,
                val pronunciations: List<PronunciationDTO>,
                val senses: List<SenseDTO>
            ) {

                @Keep
                data class SenseDTO(
                    val domains: List<DomainDTO>,
                    val examples: List<ExampleDTO>,
                    val id: String,
                    val translations: List<ExampleDTO.TranslationDTO>
                ) {

                    @Keep
                    data class DomainDTO(
                        val id: String,
                        val text: String
                    )

                    @Keep
                    data class ExampleDTO(
                        val domains: List<DomainDTO>,
                        val regions: List<RegionDTO>,
                        val registers: List<TranslationDTO.RegisterDTO>,
                        val text: String,
                        val translations: List<TranslationDTO>
                    ) {

                        @Keep
                        data class RegionDTO(
                            val id: String,
                            val text: String
                        )

                        @Keep
                        data class TranslationDTO(
                            val grammaticalFeatures: List<GrammaticalFeatureDTO>,
                            val language: String,
                            val notes: List<NoteDTO>,
                            val registers: List<RegisterDTO>,
                            val text: String
                        ) {

                            @Keep
                            data class NoteDTO(
                                val text: String,
                                val type: String
                            )

                            @Keep
                            data class RegisterDTO(
                                val id: String,
                                val text: String
                            )

                            @Keep
                            data class GrammaticalFeatureDTO(
                                val id: String,
                                val text: String,
                                val type: String
                            )
                        }
                    }
                }

                @Keep
                data class PronunciationDTO(
                    val audioFile: String,
                    val dialects: List<String>,
                    val phoneticNotation: String,
                    val phoneticSpelling: String
                )
            }

            @Keep
            data class LexicalCategoryDTO(
                val id: String,
                val text: String
            )
        }
    }
}
