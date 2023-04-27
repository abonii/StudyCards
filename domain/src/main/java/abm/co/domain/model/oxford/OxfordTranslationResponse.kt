package abm.co.domain.model.oxford

data class OxfordTranslationResponse(
    val results: List<Result>,
    val word: String
) {

    data class Result(
        val lexicalEntries: List<LexicalEntry>,
        val word: String
    ) {

        data class LexicalEntry(
            val entries: List<Entry>,
            val language: String,
            val lexicalCategory: LexicalCategory,
            val text: String
        ) {

            data class Entry(
                val etymologies: List<String>,
                val pronunciations: List<Pronunciation>,
                val senses: List<Sense>
            ) {

                data class Pronunciation(
                    val audioFile: String,
                    val dialects: List<String>,
                    val phoneticNotation: String,
                    val phoneticSpelling: String
                )

                data class Sense(
                    val domains: List<Domain>,
                    val examples: List<Example>,
                    val id: String,
                    val translations: List<Example.Translation>
                ) {

                    data class Domain(
                        val id: String,
                        val text: String
                    )

                    data class Example(
                        val domains: List<Domain>,
                        val regions: List<Region>,
                        val registers: List<Translation.Register>,
                        val text: String,
                        val translations: List<Translation>
                    ) {

                        data class Region(
                            val id: String,
                            val text: String
                        )

                        data class Translation(
                            val grammaticalFeatures: List<GrammaticalFeature>,
                            val language: String,
                            val notes: List<Note>,
                            val registers: List<Register>,
                            val text: String
                        ) {

                            data class Note(
                                val text: String,
                                val type: String
                            )

                            data class Register(
                                val id: String,
                                val text: String
                            )

                            data class GrammaticalFeature(
                                val id: String,
                                val text: String,
                                val type: String
                            )
                        }
                    }
                }
            }
            
            data class LexicalCategory(
                val id: String,
                val text: String
            )
        }
    }
}
