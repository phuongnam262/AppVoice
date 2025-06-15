package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.db.FileEntryDao

@Suppress("UNCHECKED_CAST")
class AddVocabViewModelFactory(private val fileEntryDao: FileEntryDao) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVocabViewModel::class.java)) {
            return AddVocabViewModel(fileEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 