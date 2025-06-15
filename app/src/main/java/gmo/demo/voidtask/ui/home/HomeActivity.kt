package gmo.demo.voidtask.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.BR
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.ActivityHomeBinding
import gmo.demo.voidtask.ui.base.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), HomeListener,
    KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override val layoutId: Int
        get() = R.layout.activity_home
    override val bindingVariable: Int
        get() = BR.viewmodel
    override val viewModel: HomeViewModel
        get() = ViewModelProvider(this, factory)[HomeViewModel::class.java]

    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUESTS = 1
        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Data binding
        viewModel.homeListener = this
        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        val navController = navHostFragment.navController

        intent.extras?.getString("navigateToFragment")?.let { fragmentId ->
            if (fragmentId == "addVocabFragment") {
                navController.navigate(R.id.addVocabFragment)
            } else if (fragmentId == "learnVocabFragment") {
                navController.navigate(R.id.learnVocabFragment)
            }
        }
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.homeListener = null
    }
}