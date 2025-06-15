package gmo.demo.voidtask.ui.inputotp

interface InputOTPListener {
    fun verifySuccess(email: String?)

    fun verifyFail()
}