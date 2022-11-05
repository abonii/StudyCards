package abm.co.studycards.data.model

import abm.co.studycards.data.model.vocabulary.CategoryDto

data class ParentSetDto(
    val name: String = "",
    val id: String = "",
    val categories: Map<String, CategoryDto> = HashMap()
)