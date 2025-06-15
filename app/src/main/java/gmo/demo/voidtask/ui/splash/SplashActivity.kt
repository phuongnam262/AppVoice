package gmo.demo.voidtask.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.databinding.ActivitySplashBinding
import gmo.demo.voidtask.ui.base.BaseActivity
import gmo.demo.voidtask.BR
import gmo.demo.voidtask.R
import gmo.demo.voidtask.ui.home.HomeActivity
import gmo.demo.voidtask.ui.main.MainScreenActivity
import gmo.demo.voidtask.util.CommonUtils
import gmo.demo.voidtask.util.ConstantUtils
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(),
    KodeinAware {

    override val kodein by kodein()
    private val factory: SplashViewModelFactory by instance()
    override val layoutId: Int
        get() = R.layout.activity_splash
    override val bindingVariable: Int
        get() = BR.viewmodel
    override val viewModel: SplashViewModel
        get() = ViewModelProvider(this, factory)[SplashViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtils.runTimeDelay(ConstantUtils.SPLASH_TIME_OUT.toLong()) {
            startActivity(MainScreenActivity::class.java)
            finish()
        }
    }
}