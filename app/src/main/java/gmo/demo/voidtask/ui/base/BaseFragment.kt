package gmo.demo.voidtask.ui.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import gmo.demo.voidtask.R
import gmo.demo.voidtask.util.CommonUtils
import gmo.demo.voidtask.util.Coroutines
import gmo.demo.voidtask.util.view.custom.CustomLoading

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment() {

    private var mActivity: gmo.demo.voidtask.ui.base.BaseAppCompatActivity? = null
    protected var mViewDataBinding: T? = null
        private set
    internal var mViewModel: V? = null
    private var mDialog: Dialog? = null
    private var isObserverSet = false

    /**
     * Overriding for set layoutId variable
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is gmo.demo.voidtask.ui.base.BaseAppCompatActivity) {
            mActivity = context
        }
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding?.setVariable(bindingVariable, mViewModel)
        mViewDataBinding?.lifecycleOwner = viewLifecycleOwner
        setupObservers()
    }

    protected fun navigateTo(id: Int, bundle: Bundle?) {
        try {
            Coroutines.main {
                Navigation.findNavController(mViewDataBinding?.root!!)
                    .navigate(id, bundle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Handling show loading icon, message for fragment
     */
    private fun setupObservers() {
        activity?.let { activity ->
            if (isObserverSet.not()) {
                mViewModel?.mLoading?.observe(activity) {
                    if (it != null && it) showLoading()
                    else hideLoading()
                }
                mViewModel?.mMessage?.observe(activity) {
                    it?.let {
                        mViewModel?.strMessage?.postValue(getString(it))
                        CommonUtils.showDialog(activity, "", getString(it))
                    }
                }
                mViewModel?.mMessageError?.observe(activity) {
                    it?.let {
                        mViewModel?.strMessage?.postValue(getString(it))
                        CommonUtils.showDialog(activity, "", getString(it))
                    }
                }
                mViewModel?.mErrorAllWithStatusCode?.observe(activity) {
                    it?.let { CommonUtils.showDialog(activity, "", getString(R.string.error_all, it))
                    }
                }
                isObserverSet = true
            }
        }
    }

    /**
     * Create and show progress
     */
    @SuppressLint("InflateParams")
    open fun showLoading() {
        mDialog ?: let {
            mDialog = Dialog(requireActivity())
            val inflater =
                LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_loading, null)
            mDialog?.setContentView(inflater)
            mDialog?.setCancelable(false)
            mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            mDialog?.setOnShowListener {
                mDialog?.window?.decorView?.findViewById<CustomLoading>(R.id.custom_loading)
                    ?.startAnimation()
            }
        }

        if (mDialog?.isShowing != true) {
            mDialog?.show()
        }
    }

    /**
     * hide progress
     */
    open fun hideLoading() {
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
            mDialog?.window?.decorView?.findViewById<CustomLoading>(R.id.custom_loading)
                ?.stopAnimation()
        }
    }

    /**
     * Execute backPressed
     */
    private fun doBackPressed() {
        mActivity?.onBackPressedDispatcher
    }

    /**
     * Base on activity
     */
    protected fun setFragment(fragment: Fragment) {
        mActivity?.setFragment(fragment)
    }

    /**
     * Taking rootView
     */
    private fun getRootView(): View? = mActivity?.getRootView()

    /**
     * Base on activity
     */
    protected fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }

    /**
     * Base on activity
     */
    protected fun showKeyboard(view: View) {
        mActivity?.showKeyBoard(view)
    }
}