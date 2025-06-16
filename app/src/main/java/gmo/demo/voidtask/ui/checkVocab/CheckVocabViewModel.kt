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

class CheckVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    // LiveData for UI
    val currentFrontText = MutableLiveData<String>()
    val currentBackText = MutableLiveData<String>()
    val isCardFlipped = MutableLiveData<Boolean>().apply { value = true }
    val speechStatus = MutableLiveData<String>()
    val recordingTime = MutableLiveData<String>().apply { value = "00:00" }
    val isRecording = MutableLiveData<Boolean>().apply { value = false }
    val showWaveAnimation = MutableLiveData<Boolean>().apply { value = false }

    // Private properties
    private var vocabList: List<Pair<String, String>> = emptyList()
    private var currentIndex: Int = 0
    private var speechRecognizer: SpeechRecognizer? = null
    private var timer: Timer? = null
    private var seconds: Int = 0

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
        }

        override fun onBeginningOfSpeech() {
            speechStatus.postValue(context.getString(R.string.speaking))
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            stopRecording()
        }

        override fun onError(error: Int) {
            stopRecording()
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> context.getString(R.string.audio_error)
                SpeechRecognizer.ERROR_CLIENT -> context.getString(R.string.client_error)
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> context.getString(R.string.permission_error)
                SpeechRecognizer.ERROR_NETWORK -> context.getString(R.string.network_error)
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> context.getString(R.string.network_timeout)
                SpeechRecognizer.ERROR_NO_MATCH -> context.getString(R.string.no_match)
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> context.getString(R.string.recognizer_busy)
                SpeechRecognizer.ERROR_SERVER -> context.getString(R.string.server_error)
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> context.getString(R.string.speech_timeout)
                else -> context.getString(R.string.unknown_error)
            }
            speechStatus.postValue(errorMessage)
        }

        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                if (matches.isNotEmpty()) {
                    checkPronunciation(matches[0], context)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    // Recording control
    fun startRecording(context: Context) {
        if (!checkAudioPermission(context)) {
            speechStatus.postValue(context.getString(R.string.permission_required))
            return
        }

        if (vocabList.isEmpty()) {
            speechStatus.postValue(context.getString(R.string.no_words_to_check))
            return
        }

        isRecording.value = true
        showWaveAnimation.value = true
        startTimer()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak_now))
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            stopRecording()
            speechStatus.postValue(context.getString(R.string.speech_recognition_not_available))
        }
    }

    fun stopRecording() {
        speechRecognizer?.stopListening()
        isRecording.value = false
        showWaveAnimation.value = false
        stopTimer()
    }

    // Timer functions
    private fun startTimer() {
        seconds = 0
        recordingTime.postValue("00:00")
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    seconds++
                    val minutes = seconds / 60
                    val secs = seconds % 60
                    recordingTime.postValue(String.format("%02d:%02d", minutes, secs))
                }
            }, 1000, 1000)
        }
    }

    fun resetSpeechStatus() {
        speechStatus.postValue("") // Reset về trạng thái rỗng
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        recordingTime.postValue("00:00")
    }

    // Pronunciation check
    private fun checkPronunciation(spokenText: String, context: Context) {
        val currentWord = currentFrontText.value?.lowercase()?.trim() ?: ""
        val userText = spokenText.lowercase().trim()

        val result = if (userText.contains(currentWord)) {
            "✅ ${context.getString(R.string.correct_pronunciation)}"
        } else {
            "❌ ${context.getString(R.string.wrong_pronunciation)}\n" +
                    "${context.getString(R.string.expected)}: $currentWord\n" +
                    "${context.getString(R.string.your_pronunciation)}: $userText"
        }

        speechStatus.postValue(result)
    }

    // Permission check
    private fun checkAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Vocabulary card functions
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
            resetSpeechStatus()
            resetSpeechStatus()
            currentIndex = (currentIndex - 1 + vocabList.size) % vocabList.size
            updateCardContent()
        }
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
        speechRecognizer?.destroy()
    }
}