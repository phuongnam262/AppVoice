package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.MutableLiveData
import gmo.demo.voidtask.ui.base.BaseViewModel
import gmo.demo.voidtask.data.db.FileEntryDao
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import android.util.Log
import gmo.demo.voidtask.data.entities.responses.VocabResponse
import gmo.demo.voidtask.data.repositories.AppRepository

class AddVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    private val _vocabList = MutableLiveData<List<VocabResponse>>()
    private var appRepository: AppRepository? = null

    fun setRepository(repository: AppRepository) {
        appRepository = repository
    }

    fun fetchVocabFromApiAndSave(userId: String) {
        viewModelScope.launch {
            try {
                val response = appRepository?.getVocabList(userId)
                val list = response?.vocalist ?: emptyList()
                _vocabList.postValue(list)
                // Lưu vào DB
                appRepository?.saveVocabListToDb(fileEntryDao, list)
            } catch (e: Exception) {
                Log.e("AddVocabViewModel", "Error fetching vocab: ${e.message}")
                _vocabList.postValue(emptyList())
            }
        }
    }
} 