package gmo.demo.voidtask.ui.learnVocab

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import gmo.demo.voidtask.R
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class LearnVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    val currentFrontText = MutableLiveData<String>()
    val currentBackText = MutableLiveData<String>()
    val isCardFlipped = MutableLiveData<Boolean>().apply { value = false }

    private var vocabList: List<Pair<String, String>> = emptyList()
    private var currentIndex: Int = 0
    private var textToSpeech: TextToSpeech? = null

    fun initTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    mMessage.postValue(R.string.tts_language_not_supported)
                }
            } else {
                mMessage.postValue(R.string.tts_initialization_failed)
            }
        }
    }

    fun speakCurrentWord() {
        val text = currentFrontText.value
        if (text != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    fun loadVocabFromFile() {
        viewModelScope.launch {
            mLoading.postValue(true)
            try {
                // Lấy tất cả các file từ database. Bạn có thể thêm logic chọn file cụ thể sau.
                val allFiles = fileEntryDao.getAllFileEntries() // Giả định có hàm này trong DAO
                if (allFiles.isNotEmpty()) {
                    val latestFile = allFiles.last() // Lấy file mới nhất hoặc chọn theo logic khác
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
                    val front = jsonObject.getString("front") // Giả định JSON có trường "front"
                    val back = jsonObject.getString("back") // Giả định JSON có trường "back"
                    parsedVocab.add(Pair(front, back))
                }
            } catch (e: JSONException) {
                mMessage.postValue(R.string.error_parsing_json)
                e.printStackTrace()
            }
        } else { // Assume .txt or other plain text format
            content.lines().forEach { line ->
                val parts = line.split("-", limit = 2) // Giả định định dạng "front - back"
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
            isCardFlipped.value = false // Đặt lại trạng thái lật khi chuyển thẻ
        }
    }
} 