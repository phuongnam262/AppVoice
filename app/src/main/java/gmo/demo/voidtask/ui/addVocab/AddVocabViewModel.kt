package gmo.demo.voidtask.ui.addVocab

import androidx.lifecycle.MutableLiveData
import android.net.Uri
import android.content.Intent
import gmo.demo.voidtask.ui.base.BaseViewModel

class AddVocabViewModel : BaseViewModel() {

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
} 