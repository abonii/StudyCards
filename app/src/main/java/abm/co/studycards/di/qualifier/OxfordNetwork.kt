package abm.co.studycards.di.qualifier

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class OxfordNetwork(val value: TypeEnum)