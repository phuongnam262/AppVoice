package gmo.demo.voidtask.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gmo.demo.voidtask.R
import gmo.demo.voidtask.data.network.StatusAPICode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel() {
    val mLoading = MutableLiveData<Boolean>().apply { value = false }
    val mMessage = MutableLiveData<Int>()
    val mMessageError = MutableLiveData<Int>()
    val strMessage = MutableLiveData<String>()
    val mErrorAllWithStatusCode = MutableLiveData<String>()

    // coroutines
    private var viewModelJob = Job()
    private val ioContext = viewModelJob + Dispatchers.IO // background context
    private val uiContext = viewModelJob + Dispatchers.Main // ui context
    protected val ioScope = CoroutineScope(ioContext)
    protected val uiScope = CoroutineScope(uiContext)

    /**
     * Init viewModel
     */
    open fun initViewModel() {
    }

    /**
     * Clearing viewModelJob when this viewModel already destroyed
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Handling show message
     */
    fun handleError(status: String?) {
        when (status) {
            StatusAPICode.NETWORK_ERROR.value.toString() -> mMessageError.postValue(R.string.error_network)

            StatusAPICode.EXCEPTION.value.toString() -> mMessageError.postValue(R.string.error_message)

            StatusAPICode.NO_RESPONSE.value.toString() -> mMessageError.postValue(R.string.server_error)

            else -> mErrorAllWithStatusCode.postValue(status ?: "99999")

        }
    }
}