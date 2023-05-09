package abm.co.studycards.appinfo

import abm.co.core.appinfo.ApplicationInfo
import abm.co.studycards.BuildConfig
import javax.inject.Inject

class ApplicationInfoImpl @Inject constructor() : ApplicationInfo {
    override fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getVersionCode(): String {
        return BuildConfig.VERSION_CODE
    }
}