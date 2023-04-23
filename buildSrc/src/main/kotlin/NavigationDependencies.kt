object NavigationDependencies {
    object Versions {
        const val navigation = "2.5.3"
        const val navigationCompose = "2.5.3"
        const val composeHiltNavigation = "1.0.0"
    }

    const val navigationCompose =
        "androidx.navigation:navigation-compose:${Versions.navigationCompose}"
    const val composeHiltNavigation =
        "androidx.hilt:hilt-navigation-compose:${Versions.composeHiltNavigation}"

    const val fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val ui = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val runtime = "androidx.navigation:navigation-runtime-ktx:${Versions.navigation}"
}
