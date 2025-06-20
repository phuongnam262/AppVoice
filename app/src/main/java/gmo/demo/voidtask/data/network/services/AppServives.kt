package gmo.demo.voidtask.data.network.services

import gmo.demo.voidtask.data.entities.request.VerifyOTPRequest
import gmo.demo.voidtask.data.entities.responses.BaseResponse
import gmo.demo.voidtask.data.entities.responses.ConsumerLoginResponse
import gmo.demo.voidtask.data.entities.responses.VocabListRawResponse
import gmo.demo.voidtask.data.entities.responses.VocabResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppServives {

    @POST("/api/verify-otp")
    suspend fun verifyOTP(
        @Body verifyOTPRequest: VerifyOTPRequest
    ): Response<BaseResponse<ConsumerLoginResponse>>

    @GET("/macros/s/AKfycby-lhXlx-4xaQkBwU3H7aO1DXB7V3qSB3_Hgi5RyHYaWQ-1kE80RsPegBFK9AYImfLLwQ/exec")
    suspend fun getVocabList(
        @Query("user_id") userId: String
    ): retrofit2.Response<VocabListRawResponse>

    companion object {
        fun create(retrofit: Retrofit): AppServives {
            return retrofit.create(AppServives::class.java)
        }
    }

}