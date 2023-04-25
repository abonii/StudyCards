package abm.co.studycards.di

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.studycards.navigation.NavigationBetweenModulesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindModule {
        @Binds
        abstract fun bindNavigationBetweenModules(
            impl: NavigationBetweenModulesImpl
        ): NavigationBetweenModules
    }
}
