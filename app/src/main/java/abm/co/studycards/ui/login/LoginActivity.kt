package abm.co.studycards.ui.login

import abm.co.studycards.R
import abm.co.studycards.databinding.ActivitySignBinding
import abm.co.studycards.util.base.BaseBindingActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseBindingActivity<ActivitySignBinding>(R.layout.activity_sign) {

    override fun initViews(savedInstanceState: Bundle?) {

    }

    fun setToolbar(toolbar: Toolbar, backIcon:Int) {
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = getImg(backIcon)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return true
    }
}