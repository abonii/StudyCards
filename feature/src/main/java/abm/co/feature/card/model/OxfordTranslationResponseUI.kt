package abm.co.feature.card.model

import abm.co.domain.model.oxford.OxfordTranslationResponse
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

typealias OxfordEntryUI = OxfordTranslationResponseUI.LexicalEntryUI.EntryUI

@Parcelize
@Immutable
data class OxfordTranslationResponseUI(
    val lexicalEntry: List<LexicalEntryUI>?,
    val word: String?
) : Parcelable {

    fun getOnlyTranslations(): String {
        val translations = ArrayList<String>()
        this.lexicalEntry?.forEach { lexicalEntryUI ->
            lexicalEntryUI.entries?.forEach { entryUI ->
                translations.add(entryUI.translation)
            }
        }
        return translations.joinToString("; ")
    }

    @Parcelize
    @Immutable
    data class LexicalEntryUI(
        val entries: List<EntryUI>?,
        val lexicalKind: String?
    ) : Parcelable {

        @Parcelize
        @Immutable
        data class EntryUI(
            val examples: List<ExampleUI>?,
            val translation: String,
            val id: String
        ) : Parcelable {

            @Parcelize
            @Immutable
            data class ExampleUI(
                val text: String?,
                val translation: String?
            ) : Parcelable
        }
    }
}

fun OxfordTranslationResponse.toUI() = OxfordTranslationResponseUI(
    lexicalEntry = lexicalEntry?.mapIndexed { i, lexicalEntry -> lexicalEntry.toUI(i) },
    word = word
)

fun OxfordTranslationResponse.LexicalEntry.Entry.Example.toUI() =
    OxfordTranslationResponseUI.LexicalEntryUI.EntryUI.ExampleUI(
        text = text,
        translation = translations
    )

fun OxfordTranslationResponse.LexicalEntry.Entry.toUI(index: String) =
    OxfordTranslationResponseUI.LexicalEntryUI.EntryUI(
        examples = examples?.map { it.toUI() },
        translation = translations.joinToString("; "),
        id = index
    )

fun OxfordTranslationResponse.LexicalEntry.toUI(index: Int) =
    OxfordTranslationResponseUI.LexicalEntryUI(
        entries = entries?.mapIndexed { i, it -> it.toUI("$index$i") },
        lexicalKind = lexicalKind
    )