package gmo.demo.voidtask.ui.learnVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.db.FileEntryDao

@Suppress("UNCHECKED_CAST")
class LearnVocabViewModelFactory(private val fileEntryDao: FileEntryDao) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnVocabViewModel::class.java)) {
            return LearnVocabViewModel(fileEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 