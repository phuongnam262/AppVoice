package gmo.demo.voidtask.data.repositories

import gmo.demo.voidtask.data.entities.request.VerifyOTPRequest
import gmo.demo.voidtask.data.entities.responses.BaseResponse
import gmo.demo.voidtask.data.entities.responses.ConsumerLoginResponse
import gmo.demo.voidtask.data.network.SafeApiRequest
import gmo.demo.voidtask.data.network.services.AppServives

class AppRepository (
    private val api: AppServives
) : SafeApiRequest() {

    suspend fun verifyOTP(verifyOTPRequest: VerifyOTPRequest): BaseResponse<ConsumerLoginResponse> {
        return apiRequest { api.verifyOTP(verifyOTPRequest) }
    }
}