package gmo.demo.voidtask.ui.inputotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.repositories.AppRepository

@Suppress("UNCHECKED_CAST")
class InputOTPViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InputOTPViewModel(repository) as T
    }
}