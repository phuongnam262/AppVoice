package gmo.demo.voidtask.data.network

import com.google.gson.Gson
import gmo.demo.voidtask.data.entities.responses.BaseResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.Response
import java.io.IOException

abstract class SafeApiRequest {

    /**
     * Processing call api to server by normally
     */
    protected suspend fun <T> apiRequest(
        call: suspend () -> Response<BaseResponse<T>>
    ): BaseResponse<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful && (response.body()?.code == StatusAPICode.SUCCESS.value ||
                        response.body()?.code == StatusAPICode.SUCCESSWITHOTP.value)) {
                response.body() ?: run {
                    BaseResponse.error(
                        status = StatusAPICode.NO_RESPONSE.value.toString()
                    )
                }
            } else { // http code error
                handleHttpCode(response)
            }
        } catch (e: IOException) {
            BaseResponse.error(
                status = StatusAPICode.NETWORK_ERROR.value.toString(),
                message = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(
                status = StatusAPICode.EXCEPTION.value.toString(),
                message = e.message
            )
        }
    }

    /**
     * Processing http error code
     * response: data from server
     */
    private fun <T> handleHttpCode(response: Response<BaseResponse<T>>): BaseResponse<T> {
        var errorResponse: BaseResponse<Any>? = null
        if (response.body() != null){
            response.body()?.let {
                val json = Gson().toJson(it)
                if (json.isNotEmpty()) {
                    val payloadType =
                        Types.newParameterizedType(BaseResponse::class.java, Any::class.java)
                    val jsonAdapter: JsonAdapter<BaseResponse<Any>> = moshi.adapter(payloadType)
                    errorResponse = jsonAdapter.nullSafe().fromJson(json)
                    errorResponse?.status = it.code.toString()
                }
            }
        }else {
            response.errorBody()?.let {
                val json = it.string()
                if (json.isNotEmpty()) {
                    val payloadType =
                        Types.newParameterizedType(BaseResponse::class.java, Any::class.java)
                    val jsonAdapter: JsonAdapter<BaseResponse<Any>> = moshi.adapter(payloadType)
                    errorResponse = jsonAdapter.nullSafe().fromJson(json)
                }
            }
        }
        val statusCode = if (errorResponse?.status == null) errorResponse?.code.toString() else errorResponse?.status
        return BaseResponse.error(
            status = statusCode,
            message = errorResponse?.message
        )
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }

}