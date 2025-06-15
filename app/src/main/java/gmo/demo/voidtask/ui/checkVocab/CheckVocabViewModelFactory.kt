package gmo.demo.voidtask.ui.checkVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.db.FileEntryDao

@Suppress("UNCHECKED_CAST")
class CheckVocabViewModelFactory(private val fileEntryDao: FileEntryDao) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckVocabViewModel::class.java)) {
            return CheckVocabViewModel(fileEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 