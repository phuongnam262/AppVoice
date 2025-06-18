package gmo.demo.voidtask.ui.checkVocab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import gmo.demo.voidtask.R
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity

class CheckVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    // LiveData for UI
    val currentFrontText = MutableLiveData<String>()
    val currentBackText = MutableLiveData<String>()
    val isCardFlipped = MutableLiveData<Boolean>().apply { value = false }
    val speechStatus = MutableLiveData<String>()
    val isRecording = MutableLiveData<Boolean>().apply { value = false }
    val showWaveAnimation = MutableLiveData<Boolean>().apply { value = false }

    // Private properties
    private var vocabList: List<Pair<String, String>> = emptyList()
    private var currentIndex: Int = 0
    private var speechRecognizer: SpeechRecognizer? = null
    private var recordedText: String = ""

    // Speech Recognition setup
    fun setupSpeechRecognizer(context: Context) {
        if (speechRecognizer == null) {
            speechRecognizer = createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener(context))
            }
        }
    }

    private fun createRecognitionListener(context: Context) = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            speechStatus.postValue(context.getString(R.string.listening))
            showWaveAnimation.postValue(true)
        }

        override fun onBeginningOfSpeech() {
            speechStatus.postValue(context.getString(R.string.speaking))
            showWaveAnimation.postValue(true)
        }

        override fun onRmsChanged(rmsdB: Float) {
            showWaveAnimation.postValue(true)
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Không cần thay đổi trạng thái ở đây
        }

        override fun onEndOfSpeech() {
            showWaveAnimation.postValue(false)
        }

        override fun onError(error: Int) {
            showWaveAnimation.postValue(false)
            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH -> {
                    val currentWord = currentFrontText.value?.lowercase()?.trim() ?: ""
                    speechStatus.postValue("❌ ${context.getString(R.string.wrong_pronunciation)}\n" +
                            "${context.getString(R.string.expected)}: $currentWord\n" +
                            "${context.getString(R.string.your_pronunciation)}: Không nhận dạng được")
                }
                else -> {
                    resetSpeechStatus()
                }
            }
        }

        override fun onResults(results: Bundle?) {
            showWaveAnimation.postValue(false)
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                if (matches.isNotEmpty()) {
                    recordedText = matches[0]
                    checkPronunciation(recordedText, context)
                } else {
                    val currentWord = currentFrontText.value?.lowercase()?.trim() ?: ""
                    speechStatus.postValue("❌ ${context.getString(R.string.wrong_pronunciation)}\n" +
                            "${context.getString(R.string.expected)}: $currentWord\n" +
                            "${context.getString(R.string.your_pronunciation)}: Không nhận dạng được")
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            if (speechStatus.value == context.getString(R.string.speaking)) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                    if (matches.isNotEmpty()) {
                        speechStatus.postValue("${context.getString(R.string.speaking)}: ${matches[0]}")
                    }
                }
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun toggleRecording(context: Context) {
        if (isRecording.value == true) {
            stopRecording()
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                (context as? AppCompatActivity)?.let { activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSION_REQUEST_RECORD_AUDIO
                    )
                }
                return
            }
            startRecording(context)
        }
    }

    internal fun startRecording(context: Context) {
        if (!checkAudioPermission(context)) {
            speechStatus.postValue(context.getString(R.string.permission_required))
            return
        }

        if (vocabList.isEmpty()) {
            speechStatus.postValue(context.getString(R.string.no_words_to_check))
            return
        }

        resetSpeechStatus()
        recordedText = ""

        if (speechRecognizer == null) {
            setupSpeechRecognizer(context)
        }

        isRecording.value = true
        showWaveAnimation.value = true

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak_now))
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            stopRecording()
            setupSpeechRecognizer(context)
        }
    }

    private fun stopRecording() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isRecording.value = false
        showWaveAnimation.value = false
    }

    private fun checkPronunciation(spokenText: String, context: Context) {
        val currentWord = currentFrontText.value?.lowercase()?.trim() ?: ""
        val userText = spokenText.lowercase().trim()

        val result = if (userText == currentWord) {
            "✅ ${context.getString(R.string.correct_pronunciation)}"
        } else {
            "❌ ${context.getString(R.string.wrong_pronunciation)}\n" +
                    "${context.getString(R.string.expected)}: $currentWord\n" +
                    "${context.getString(R.string.your_pronunciation)}: $userText"
        }
        speechStatus.postValue(result)
    }

    private fun checkAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun resetSpeechStatus() {
        speechStatus.postValue("")
    }

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
        isCardFlipped.value = !(isCardFlipped.value ?: true)
    }

    fun showNextCard() {
        if (vocabList.isNotEmpty()) {
            resetAllStates()
            currentIndex = (currentIndex + 1) % vocabList.size
            updateCardContent()
        }
    }

    fun showPreviousCard() {
        if (vocabList.isNotEmpty()) {
            resetAllStates()
            currentIndex = (currentIndex - 1 + vocabList.size) % vocabList.size
            updateCardContent()
        }
    }

    private fun resetAllStates() {
        stopRecording()
        resetSpeechStatus()
        isCardFlipped.value = true
        showWaveAnimation.value = false
    }

    private fun updateCardContent() {
        if (vocabList.isNotEmpty()) {
            resetSpeechStatus()
            val currentCard = vocabList[currentIndex]
            currentFrontText.value = currentCard.first
            currentBackText.value = currentCard.second
            isCardFlipped.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val PERMISSION_REQUEST_RECORD_AUDIO = 1001
    }
}
