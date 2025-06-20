package gmo.demo.voidtask.data.repositories

import gmo.demo.voidtask.data.entities.request.VerifyOTPRequest
import gmo.demo.voidtask.data.entities.responses.BaseResponse
import gmo.demo.voidtask.data.entities.responses.ConsumerLoginResponse
import gmo.demo.voidtask.data.entities.responses.VocabListRawResponse
import gmo.demo.voidtask.data.network.SafeApiRequest
import gmo.demo.voidtask.data.network.services.AppServives
import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.data.db.FileEntry
import com.google.gson.Gson
import gmo.demo.voidtask.data.entities.responses.VocabResponse

class AppRepository (
    private val api: AppServives
) : SafeApiRequest() {

    suspend fun verifyOTP(verifyOTPRequest: VerifyOTPRequest): BaseResponse<ConsumerLoginResponse> {
        return apiRequest { api.verifyOTP(verifyOTPRequest) }
    }

    suspend fun getVocabList(userId: String): VocabListRawResponse? {
        val response = api.getVocabList(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun saveVocabListToDb(fileEntryDao: FileEntryDao, vocabList: List<VocabResponse>) {
        val fileName = "vocab_api.json"
        val json = Gson().toJson(vocabList)
        val fileEntry = FileEntry(fileName = fileName, fileContent = json)
        fileEntryDao.insertFileEntry(fileEntry)
    }
}