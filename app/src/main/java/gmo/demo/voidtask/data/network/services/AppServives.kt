package gmo.demo.voidtask.data.network.services

import gmo.demo.voidtask.data.entities.request.VerifyOTPRequest
import gmo.demo.voidtask.data.entities.responses.BaseResponse
import gmo.demo.voidtask.data.entities.responses.ConsumerLoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface AppServives {

    @POST("/api/verify-otp")
    suspend fun verifyOTP(
        @Body verifyOTPRequest: VerifyOTPRequest
    ): Response<BaseResponse<ConsumerLoginResponse>>

    companion object {
        fun create(retrofit: Retrofit): AppServives {
            return retrofit.create(AppServives::class.java)
        }
    }

}