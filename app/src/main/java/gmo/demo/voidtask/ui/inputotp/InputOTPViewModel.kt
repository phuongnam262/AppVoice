package gmo.demo.voidtask.ui.inputotp

import androidx.lifecycle.MutableLiveData
import gmo.demo.voidtask.R
import gmo.demo.voidtask.data.entities.request.VerifyOTPRequest
import gmo.demo.voidtask.data.preference.PreferenceHelper
import gmo.demo.voidtask.data.repositories.AppRepository
import gmo.demo.voidtask.ui.base.BaseViewModel
import gmo.demo.voidtask.util.ConstantUtils
import kotlinx.coroutines.launch

class  InputOTPViewModel(
    private val managerRepository: AppRepository
) : BaseViewModel() {

    var inputOTPListener: InputOTPListener? = null
    var otpText = MutableLiveData<String>()
    var email = MutableLiveData<String>()

    fun onSendOTP() {
        ioScope.launch {
            if (otpText.value.isNullOrEmpty()) {
                mMessageError.postValue(R.string.error_otp_emplty)
                inputOTPListener?.verifyFail()
                return@launch
            }
            mLoading.postValue(true)
            val param = VerifyOTPRequest()
            param.email = email.value
            param.otp = otpText.value
            managerRepository.verifyOTP(param).apply {
                if (isSuccessful) {
                    if (data != null ) {
                        PreferenceHelper.writeString(ConstantUtils.API_TOKEN, data.token)
                        mMessageError.postValue(R.string.error_null)
                        inputOTPListener?.verifySuccess(email.value)
                    }
                } else {
                    handleError(status)
                    inputOTPListener?.verifyFail()
                }
            }
        }.invokeOnCompletion { mLoading.postValue(false) }
    }
}