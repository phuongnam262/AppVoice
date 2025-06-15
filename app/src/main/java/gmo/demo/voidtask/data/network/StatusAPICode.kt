package gmo.demo.voidtask.data.network

/**
 * Enum type for status code
 */
enum class StatusAPICode(val value: Int) {
    SUCCESS(200),
    SUCCESSWITHOTP(201),
    NETWORK_ERROR(99),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504),
    NO_RESPONSE(999),
    EXCEPTION(998)

}