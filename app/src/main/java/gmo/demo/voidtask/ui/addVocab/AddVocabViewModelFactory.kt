package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.data.repositories.AppRepository

@Suppress("UNCHECKED_CAST")
class AddVocabViewModelFactory(private val fileEntryDao: FileEntryDao, private val appRepository: AppRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVocabViewModel::class.java)) {
            val viewModel = AddVocabViewModel(fileEntryDao)
            viewModel.setRepository(appRepository)
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 