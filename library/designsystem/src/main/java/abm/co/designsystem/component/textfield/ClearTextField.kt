package abm.co.designsystem.component.textfield

import abm.co.designsystem.component.button.IconButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.domain.functional.safeLet
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ClearTextField(
    hint: String,
    value: State<String>,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    onClickEndIcon: (() -> Unit)? = null,
    @DrawableRes endIconRes: Int? = null,
    singleLine: Boolean = true
) {
    BasicTextField(
        modifier = modifier,
        value = value.value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                if (value.value.isEmpty()) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = hint,
                        style = textStyle.copy(
                            color = StudyCardsTheme.colors.textSecondary
                        ),
                        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    safeLet(onClickEndIcon, endIconRes) { onClick, icon ->
                        IconButton(
                            iconRes = icon,
                            onClick = onClick,
                            contentColor = textStyle.color
                        )
                    }
                }
            }
        },
        cursorBrush = SolidColor(textStyle.color)
    )
}
