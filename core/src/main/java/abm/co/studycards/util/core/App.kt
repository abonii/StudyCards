package abm.co.studycards.util.core

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
