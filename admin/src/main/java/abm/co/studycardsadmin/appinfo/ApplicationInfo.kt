package abm.co.studycardsadmin.appinfo

import abm.co.core.appinfo.ApplicationInfo
import abm.co.studycardsadmin.BuildConfig
import javax.inject.Inject

class ApplicationInfoImpl @Inject constructor() : ApplicationInfo {
    override fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getVersionCode(): String {
        return BuildConfig.VERSION_CODE
    }
}