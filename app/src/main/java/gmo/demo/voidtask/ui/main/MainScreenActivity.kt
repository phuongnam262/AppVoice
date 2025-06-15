package gmo.demo.voidtask.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.BR
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.ActivityMainScreenBinding
import gmo.demo.voidtask.ui.base.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import android.content.Intent
import gmo.demo.voidtask.ui.home.HomeActivity

class MainScreenActivity : BaseActivity<ActivityMainScreenBinding, MainScreenViewModel>(), KodeinAware {
    override val kodein by kodein()
    // ViewModelFactory không có tham số, nên không cần instance() từ Kodein
    private val factory: MainScreenViewModelFactory = MainScreenViewModelFactory()
    override val layoutId: Int get() = R.layout.activity_main_screen
    override val bindingVariable: Int get() = BR.viewmodel
    override val viewModel: MainScreenViewModel by lazy {
        ViewModelProvider(this, factory)[MainScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Thiết lập listener cho các nút nếu cần
        mViewDataBinding?.btnNhapTuVung?.setOnClickListener {
            // Xử lý sự kiện "Nhập từ vựng"
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("navigateToFragment", "addVocabFragment")
            startActivity(intent)
        }
        mViewDataBinding?.btnHocTuVung?.setOnClickListener {
            // Xử lý sự kiện "Học từ vựng"
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("navigateToFragment", "learnVocabFragment")
            startActivity(intent)
        }
        mViewDataBinding?.btnKiemTra?.setOnClickListener {
            // Xử lý sự kiện "Kiểm tra"
        }
    }
}