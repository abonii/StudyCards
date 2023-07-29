package abm.co.data.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.CardKind
import abm.co.domain.model.Category
import abm.co.domain.model.explore.ExploreCategoryContext
import abm.co.domain.repository.RedesignServerRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

class RedesignServerRepositoryImplTest @Inject constructor() : RedesignServerRepository {
    override suspend fun getExploreCategories(): Either<Failure, List<Category>> {
        delay(1200)
        return Either.Right(
            buildList {
                repeat(20) {
                    add(
                        Category(
                            title = "Category Title",
                            cardsCount = 10,
                            bookmarked = Random.nextBoolean(),
                            creatorName = null,
                            creatorID = null,
                            imageURL = "https://catherineasquithgallery.com/uploads/posts/2021-03/1614612233_137-p-fon-dlya-fotoshopa-priroda-209.jpg",
                            id = it.toString()
                        )
                    )
                }
            }
        )
    }

    override suspend fun getExploreCategory(id: String): Either<Failure, ExploreCategoryContext> {
        delay(1200)
        return Either.Right(
            ExploreCategoryContext(
                category = Category(
                    title = "Category Title",
                    cardsCount = 10,
                    bookmarked = Random.nextBoolean(),
                    creatorName = null,
                    creatorID = null,
                    imageURL = "https://catherineasquithgallery.com/uploads/posts/2021-03/1614612233_137-p-fon-dlya-fotoshopa-priroda-209.jpg",
                    id = 0.toString()
                ),
                cards = buildList {
                    repeat(20) {
                        add(
                            Card(
                                name = "Card Title",
                                imageUrl = "https://catherineasquithgallery.com/uploads/posts/2021-03/1614612233_137-p-fon-dlya-fotoshopa-priroda-209.jpg",
                                id = it.toString(),
                                translation = "translation",
                                example = "example",
                                kind = CardKind.UNDEFINED,
                                learnedPercent = Random.nextFloat(),
                                categoryID = id,
                                repeatedCount = 1,
                                nextRepeatTime = 0
                            )
                        )
                    }
                }
            )
        )
    }

    override suspend fun addExploreCategoryToUserCategory(request: ExploreCategoryContext): Either<Failure, Unit> {
        delay(1200)
        return Either.Empty
    }

}
