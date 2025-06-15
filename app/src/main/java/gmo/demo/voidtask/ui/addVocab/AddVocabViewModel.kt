package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import android.content.Intent
import gmo.demo.voidtask.ui.base.BaseViewModel
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.data.db.FileEntry
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import android.content.ContentResolver
import android.util.Log
import gmo.demo.voidtask.R
import java.io.IOException

class AddVocabViewModel(private val fileEntryDao: FileEntryDao) : BaseViewModel() {

    val selectedFileUri = MutableLiveData<Uri>()

    fun onAddFolderClicked(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val mimeTypes = arrayOf("text/plain", "application/json")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return intent
    }

    fun onFileSelected(uri: Uri?) {
        uri?.let { selectedFileUri.value = it }
    }

    fun saveFileToDatabase(fileName: String, uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val fileContent = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (!fileContent.isNullOrEmpty()) {
                    val fileEntry = FileEntry(fileName = fileName, fileContent = fileContent)
                    fileEntryDao.insertFileEntry(fileEntry)
                    Log.d("AddVocabViewModel", "File saved to database: $fileName")
                    mMessage.postValue(R.string.file_saved_successfully) // Giả định bạn có string resource này
                } else {
                    Log.d("AddVocabViewModel", "File content is empty or null.")
                    mMessage.postValue(R.string.file_content_empty) // Giả định bạn có string resource này
                }
            } catch (e: IOException) {
                Log.e("AddVocabViewModel", "Error reading file: ${e.message}")
                mMessage.postValue(R.string.error_reading_file) // Giả định bạn có string resource này
            } catch (e: Exception) {
                Log.e("AddVocabViewModel", "Error saving file to database: ${e.message}")
                mMessage.postValue(R.string.error_saving_file) // Giả định bạn có string resource này
            }
        }
    }
} 