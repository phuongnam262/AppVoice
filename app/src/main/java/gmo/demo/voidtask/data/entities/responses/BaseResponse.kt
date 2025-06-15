package gmo.demo.voidtask.data.entities.responses

/**
 * Structure response
 * T: type variable for response data
 */

data class BaseResponse <T>(
    val isSuccessful: Boolean = true, // custom field
    var status: String? = null,
    val message: String? = null,
    val data: T? = null,
    var code: Int? = null
) {
    companion object {
        fun <T> error(
            isSuccessful: Boolean = false,
            status: String? = null,
            message: String? = null,
            data: T? = null,
            code: Int? = null,
        ) = BaseResponse<T>(
            isSuccessful = isSuccessful,
            status = status,
            message = message,
            data = data,
            code =code
        )
    }
}
