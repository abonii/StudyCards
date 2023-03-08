package abm.co.data.model.oxford

import androidx.annotation.Keep

@Keep
data class OxfordEntryResponseDTO(
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
                data class PronunciationDTO(
                    val audioFile: String,
                    val dialects: List<String>,
                    val phoneticNotation: String,
                    val phoneticSpelling: String
                )

                @Keep
                data class SenseDTO(
                    val definitions: List<String>,
                    val domains: List<DomainDTO>,
                    val examples: List<ExampleDTO>,
                    val id: String,
                    val subsenses: List<SubsenseDTO>,
                    val variantForms: List<VariantFormDTO>
                ) {

                    @Keep
                    data class DomainDTO(
                        val id: String,
                        val text: String
                    )

                    @Keep
                    data class ExampleDTO(
                        val notes: List<NoteDTO>,
                        val text: String
                    ) {

                        @Keep
                        data class NoteDTO(
                            val text: String,
                            val type: String
                        )
                    }

                    @Keep
                    data class VariantFormDTO(
                        val text: String
                    )

                    @Keep
                    data class SubsenseDTO(
                        val definitions: List<String>,
                        val domains: List<DomainDTO>,
                        val examples: List<ExampleDTO>,
                        val id: String,
                        val regions: List<RegionDTO>,
                        val registers: List<RegisterDTO>
                    ) {

                        @Keep
                        data class RegionDTO(
                            val id: String,
                            val text: String
                        )

                        @Keep
                        data class RegisterDTO(
                            val id: String,
                            val text: String
                        )
                    }
                }
            }

            @Keep
            data class LexicalCategoryDTO(
                val id: String,
                val text: String
            )
        }
    }
}