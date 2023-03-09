package abm.co.designsystem.component.textfield

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsShape
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.theme.StudyCardsTypography
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextFieldWithLabel(
    label: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Box(modifier = modifier) {
        val textInput = StudyCardsTheme.colors.textOnyx
        val textColor = if (value.isEmpty()) colorResource(id = R.color.text_hint) else textInput
        val customTextSelectionColors = TextSelectionColors(
            handleColor = textInput,
            backgroundColor = textInput.copy(alpha = 0.4f)
        )
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = StudyCardsTheme.typography.weight400Size16LineHeight20,
                visualTransformation = if (value.isEmpty())
                    PlaceholderTransformation(hint)
                else visualTransformation,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                shape = StudyCardsTheme.shapes.shapeBig,
                singleLine = true,
                isError = isError,
                label = {
                    Text(
                        text = label,
                        style = StudyCardsTheme.typography.weight400Size14LineHeight18.copy(
                            color = colorResource(id = R.color.text_secondary)
                        )
                    )
                },
                placeholder = {
                    Text(
                        text = hint,
                        style = StudyCardsTheme.typography.weight400Size16LineHeight20.copy(
                            color = textColor
                        )
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = textColor,
                    cursorColor = textInput,
                    unfocusedBorderColor = colorResource(id = R.color.fill_stroke),
                    focusedBorderColor = colorResource(id = R.color.fill_primary)
                )
            )
        }
    }
}
