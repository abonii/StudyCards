package abm.co.domain.model.oxford

data class OxfordTranslationResponse(
    val lexicalEntry: List<LexicalEntry>?,
    val word: String?
) {
    data class LexicalEntry(
        val entries: List<Entry>?,
        val lexicalKind: String?
    ) {

        data class Entry(
            val examples: List<Example>?,
            val translations: List<String>
        ) {

            data class Example(
                val text: String?,
                val translations: String?
            )
        }
    }
}

