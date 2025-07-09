package gmo.demo.voidtask.data.network.services

import gmo.demo.voidtask.data.entities.responses.VocabListRawResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface AppServives {
    @GET("/")
    suspend fun getVocabList(): Response<VocabListRawResponse>

    companion object {
        fun create(retrofit: Retrofit): AppServives {
            return retrofit.create(AppServives::class.java)
        }
    }
}