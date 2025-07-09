package gmo.demo.voidtask.data.repositories

import gmo.demo.voidtask.data.db.FileEntryDao
import gmo.demo.voidtask.data.entities.responses.VocabListRawResponse
import gmo.demo.voidtask.data.entities.responses.VocabResponse
import gmo.demo.voidtask.data.network.AppServives

class AppRepository (
    private val api: AppServives
) {
    suspend fun getVocabList(): VocabListRawResponse? {
        val response = api.getVocabList()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun saveVocabListToDb(fileEntryDao: FileEntryDao, vocabList: List<VocabResponse>) {
        for (vocab in vocabList) {
            fileEntryDao.insertFileEntry(
                gmo.demo.voidtask.data.db.FileEntry(
                    fileName = vocab.english ?: "",
                    fileContent = vocab.vietnamese ?: ""
                )
            )
        }
    }
} 