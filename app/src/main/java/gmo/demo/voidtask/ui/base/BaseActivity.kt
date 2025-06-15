package gmo.demo.voidtask.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import gmo.demo.voidtask.R
import gmo.demo.voidtask.util.CommonUtils

/**
 * process base logic for all activities
 */
abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : gmo.demo.voidtask.ui.base.BaseAppCompatActivity(){

    internal var mViewDataBinding: T? = null
    private var mViewModel: V? = null

    /**
     * Overriding for set LayoutId
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int?

    /**
     * Overriding for set binding variable
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * Overriding for set view model
     * @return view model instance
     */
    abstract val viewModel: V

    private var isObserverSet = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        setupObservers()
    }

    /**
     * Processing binding data
     */
    private fun performDataBinding() {
        mViewDataBinding = layoutId?.let { DataBindingUtil.setContentView(this, it) }
        //
        mViewModel = if (mViewModel == null) viewModel else mViewModel
        mViewDataBinding?.setVariable(bindingVariable, mViewModel)
        mViewDataBinding?.lifecycleOwner = this
        mViewDataBinding?.executePendingBindings()
    }

    /**
     * Handling show loading icon, message, snackbar
     */
    private fun setupObservers() {
        if (isObserverSet.not()) {
            mViewModel?.mLoading?.observe(this) {
                if (it != null && it) showLoading()
                else hideLoading()
            }
            mViewModel?.mMessage?.observe(this) {
                it?.let { CommonUtils.showDialog(this, "", getString(it)) }
            }
            mViewModel?.mMessageError?.observe(this) {
                it?.let { CommonUtils.showDialog(this, "", getString(it)) }
            }
            mViewModel?.mErrorAllWithStatusCode?.observe(this) {
                it?.let { CommonUtils.showDialog(this, "", getString(R.string.error_all, it))
                }
            }
            isObserverSet = true
        }
    }
}