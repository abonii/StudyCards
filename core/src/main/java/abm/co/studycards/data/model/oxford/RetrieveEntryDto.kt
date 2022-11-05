package abm.co.studycards.data.model.oxford

data class RetrieveEntryDto(
    val word: String?,
    val results: List<ResultsEntryDto>
)
