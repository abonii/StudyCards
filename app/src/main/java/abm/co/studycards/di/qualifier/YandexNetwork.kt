package abm.co.studycards.di.qualifier

import java.lang.annotation.Documented
import javax.inject.Qualifier

@Qualifier
@Documented
@Retention(AnnotationRetention.RUNTIME)
annotation class YandexNetwork(val value: TypeEnum)