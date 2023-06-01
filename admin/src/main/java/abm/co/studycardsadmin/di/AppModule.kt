package abm.co.studycardsadmin.di

import abm.co.core.appinfo.ApplicationInfo
import abm.co.core.navigation.NavigationBetweenModules
import abm.co.studycardsadmin.appinfo.ApplicationInfoImpl
import abm.co.studycardsadmin.navigation.NavigationBetweenModulesImpl
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
        @Binds
        abstract fun bindApplicationInfo(
            impl: ApplicationInfoImpl
        ): ApplicationInfo
    }
}
