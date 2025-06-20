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
import java.util.regex.Pattern

class LearnVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    val currentFrontText = MutableLiveData<String>()
    val currentBackText = MutableLiveData<String>()
    val isCardFlipped = MutableLiveData<Boolean>().apply { value = false }
    val currentPhienamText = MutableLiveData<String>()

    private var vocabList: List<Triple<String, String, String>> = emptyList()
    private var currentIndex: Int = 0
    private var textToSpeech: TextToSpeech? = null

    fun initTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set default language to English
                textToSpeech?.setLanguage(Locale.US)
                // Tự động phát âm từ đầu tiên sau khi khởi tạo thành công
                if (currentFrontText.value != null) {
                    speakCurrentWord()
                }
            } else {
                mMessage.postValue(R.string.tts_initialization_failed)
            }
        }
    }

    private fun isEnglishText(text: String): Boolean {
        // Pattern to match English text (letters, numbers, and common punctuation)
        val englishPattern = Pattern.compile("^[a-zA-Z0-9\\s.,!?'\"()-]+$")
        return englishPattern.matcher(text).matches()
    }

    fun speakCurrentWord() {
        val text = if (isCardFlipped.value == true) {
            currentBackText.value
        } else {
            currentFrontText.value
        }
        
        if (text != null) {
            // Set language based on text content
            val locale = if (isEnglishText(text)) {
                Locale.US
            } else {
                Locale("vi", "VN")
            }
            
            textToSpeech?.setLanguage(locale)
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
                // Lấy tất cả các file từ database. Ưu tiên file vocab_api.json
                val allFiles = fileEntryDao.getAllFileEntries()
                val vocabFile = allFiles.find { it.fileName == "vocab_api.json" } ?: allFiles.lastOrNull()
                if (vocabFile != null) {
                    vocabList = parseFileContent(vocabFile.fileContent, vocabFile.fileName)
                    if (vocabList.isNotEmpty()) {
                        currentIndex = 0
                        updateCardContent()
                        // Tự động phát âm từ đầu tiên sau khi tải xong
                        if (textToSpeech != null) {
                            speakCurrentWord()
                        }
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

    private fun parseFileContent(content: String, fileName: String): List<Triple<String, String, String>> {
        val parsedVocab = mutableListOf<Triple<String, String, String>>()
        if (fileName == "vocab_api.json") {
            try {
                val jsonArray = JSONArray(content)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val front = jsonObject.getString("english")
                    val back = jsonObject.getString("vietnamese")
                    val phienam = jsonObject.optString("phienam", "")
                    parsedVocab.add(Triple(front, back, phienam))
                }
            } catch (e: JSONException) {
                mMessage.postValue(R.string.error_parsing_json)
                e.printStackTrace()
            }
        } else if (fileName.endsWith(".json", ignoreCase = true)) {
            try {
                val jsonArray = JSONArray(content)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val front = jsonObject.getString("front")
                    val back = jsonObject.getString("back")
                    parsedVocab.add(Triple(front, back, ""))
                }
            } catch (e: JSONException) {
                mMessage.postValue(R.string.error_parsing_json)
                e.printStackTrace()
            }
        } else { // Assume .txt or other plain text format
            content.lines().forEach { line ->
                val parts = line.split("-", limit = 2)
                if (parts.size == 2) {
                    parsedVocab.add(Triple(parts[0].trim(), parts[1].trim(), ""))
                }
            }
        }
        return parsedVocab
    }

    fun flipCard() {
        isCardFlipped.value = !(isCardFlipped.value ?: false)
        speakCurrentWord()
    }

    fun showNextCard() {
        if (vocabList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % vocabList.size
            updateCardContent()
            // Đọc từ mới sau khi chuyển thẻ
            speakCurrentWord()
        }
    }

    fun showPreviousCard() {
        if (vocabList.isNotEmpty()) {
            currentIndex = (currentIndex - 1 + vocabList.size) % vocabList.size
            updateCardContent()
            // Đọc từ mới sau khi chuyển thẻ
            speakCurrentWord()
        }
    }

    private fun updateCardContent() {
        if (vocabList.isNotEmpty()) {
            val currentCard = vocabList[currentIndex]
            currentFrontText.value = currentCard.first
            currentBackText.value = currentCard.second
            currentPhienamText.value = currentCard.third
            isCardFlipped.value = false // Đặt lại trạng thái lật khi chuyển thẻ
        }
    }
} 