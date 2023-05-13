package abm.co.feature.userattributes.lanugage

import abm.co.designsystem.component.modifier.Modifier
import abm.co.feature.userattributes.common.AnimatableContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LanguageItems(
    visible: Boolean,
    languages: ImmutableList<LanguageUI>,
    isToRight: Boolean,
    onClickItem: (LanguageUI) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatableContent(
        visible = visible,
        isToRight = isToRight
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            languages.forEach { language ->
                LanguageItem(
                    language = language,
                    onClick = {
                        onClickItem(language)
                    }
                )
            }
        }
    }
}
