package gmo.demo.voidtask.ui.inputotp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.BR
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentInputOtpBinding
import gmo.demo.voidtask.ui.base.BaseFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class InputOTPFragment : BaseFragment<FragmentInputOtpBinding, InputOTPViewModel>(), KodeinAware,
    View.OnClickListener, InputOTPListener {

    override val kodein by kodein()
    private val factory: InputOTPViewModelFactory by instance()
    override val layoutId: Int = R.layout.fragment_input_otp
    override val bindingVariable: Int
        get() = BR.viewmodel

    override val viewModel: InputOTPViewModel
        get() = ViewModelProvider(this, factory)[InputOTPViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.inputOTPListener = this
        initView()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.inputOTPListener = null
    }

    private fun initView(){
        mViewDataBinding?.btnSubmit?.setOnClickListener(this)
    }

    private fun initData(){
        viewModel.email.value = "test@gmail.com"
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_submit -> {
                viewModel.onSendOTP()
            }
        }
    }

    override fun verifySuccess(email: String?) {
        viewModel.otpText.postValue("")
    }

    override fun verifyFail() {
    }
}