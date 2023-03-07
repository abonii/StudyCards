package abm.co.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import abm.co.navigation.Destinations
import abm.co.navigation.extension_function.parcelableData
import ir.kaaveh.designsystem.base.BaseViewModel
import ir.kaaveh.domain.model.News
import ir.kaaveh.newsdetail.NewsDetailRoute

fun NavGraphBuilder.newsDetail(
    onProvideBaseViewModel: (baseViewModel: BaseViewModel) -> Unit,
) {
    composable(
        route = Destinations.NewsDetailScreen().route,
    ) { entry ->
        val news = entry.parcelableData<News>(Destinations.NewsDetailScreen().news)
        NewsDetailRoute(
            news = news,
            onProvideBaseViewModel = {
                onProvideBaseViewModel(it)
            },
        )
    }
}