package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AddVocabViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVocabViewModel::class.java)) {
            return AddVocabViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 