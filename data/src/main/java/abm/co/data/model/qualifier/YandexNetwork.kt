package abm.co.data.model.qualifier

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class YandexNetwork(val value: TypeEnum)