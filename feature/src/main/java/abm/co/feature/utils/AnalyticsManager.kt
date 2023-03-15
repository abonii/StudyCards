package abm.co.feature.utils

import abm.co.feature.BuildConfig
import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsManager {
    fun sendEvent(name: String, params: Bundle? = null) {
        if(!BuildConfig.DEBUG){
            Firebase.analytics.logEvent(name, params)
        }
    }
}
