package abm.co.studycardsadmin

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.mocklets.pluto.Pluto
import com.mocklets.pluto.PlutoLog
import com.mocklets.pluto.modules.exceptions.ANRException
import com.mocklets.pluto.modules.exceptions.ANRListener
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudyCardsAdminApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        initPluto()
    }

    private fun initPluto() {
        Pluto.initialize(this)
        Pluto.setANRListener(object : ANRListener {
            override fun onAppNotResponding(exception: ANRException) {
                exception.printStackTrace()
                PlutoLog.e("anr-exception", exception.threadStateMap)
            }
        })
    }
}
