package abm.co.designsystem.component.textfield

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Immutable
class PlaceholderTransformation(private val placeholder: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return 0
            }
        }
        return TransformedText(AnnotatedString(placeholder), numberOffsetTranslator)
    }
}