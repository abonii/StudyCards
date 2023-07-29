package abm.co.domain.model.explore

import abm.co.domain.model.Card
import abm.co.domain.model.Category

data class ExploreCategoryContext(
    val category: Category,
    val cards: List<Card>
)
