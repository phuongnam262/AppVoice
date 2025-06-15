package gmo.demo.voidtask.ui.checkVocab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import gmo.demo.voidtask.R
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class CheckVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    val currentFrontText = MutableLiveData<String>()
    val currentBackText = MutableLiveData<String>()
    val isCardFlipped = MutableLiveData<Boolean>().apply { value = true } // Khởi tạo là true để hiển thị tiếng Việt trước

    private var vocabList: List<Pair<String, String>> = emptyList()
    private var currentIndex: Int = 0

    fun loadVocabFromFile() {
        viewModelScope.launch {
            mLoading.postValue(true)
            try {
                val allFiles = fileEntryDao.getAllFileEntries()
                if (allFiles.isNotEmpty()) {
                    val latestFile = allFiles.last()
                    vocabList = parseFileContent(latestFile.fileContent, latestFile.fileName)
                    if (vocabList.isNotEmpty()) {
                        currentIndex = 0
                        updateCardContent()
                    } else {
                        mMessage.postValue(R.string.no_vocab_found)
                    }
                } else {
                    mMessage.postValue(R.string.no_files_found)
                }
            } catch (e: Exception) {
                mMessage.postValue(R.string.error_loading_vocab)
                e.printStackTrace()
            }
            mLoading.postValue(false)
        }
    }

    private fun parseFileContent(content: String, fileName: String): List<Pair<String, String>> {
        val parsedVocab = mutableListOf<Pair<String, String>>()
        if (fileName.endsWith(".json", ignoreCase = true)) {
            try {
                val jsonArray = JSONArray(content)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val front = jsonObject.getString("front")
                    val back = jsonObject.getString("back")
                    parsedVocab.add(Pair(front, back))
                }
            } catch (e: JSONException) {
                mMessage.postValue(R.string.error_parsing_json)
                e.printStackTrace()
            }
        } else {
            content.lines().forEach { line ->
                val parts = line.split("-", limit = 2)
                if (parts.size == 2) {
                    parsedVocab.add(Pair(parts[0].trim(), parts[1].trim()))
                }
            }
        }
        return parsedVocab
    }

    fun flipCard() {
        isCardFlipped.value = !(isCardFlipped.value ?: false)
    }

    fun showNextCard() {
        if (vocabList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % vocabList.size
            updateCardContent()
        }
    }

    fun showPreviousCard() {
        if (vocabList.isNotEmpty()) {
            currentIndex = (currentIndex - 1 + vocabList.size) % vocabList.size
            updateCardContent()
        }
    }

    private fun updateCardContent() {
        if (vocabList.isNotEmpty()) {
            val currentCard = vocabList[currentIndex]
            currentFrontText.value = currentCard.first
            currentBackText.value = currentCard.second
            isCardFlipped.value = true // Đặt lại trạng thái lật để hiển thị mặt sau (tiếng Việt) trước
        }
    }
} 