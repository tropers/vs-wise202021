package middleware

/**
 * MessageType designates the different messages exchanged between services
 */
enum class MessageType {
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    SET_COORDINATOR_REQUEST,
    SET_COORDINATOR_RESPONSE,
    ELECTION,
    GET_WELDING_COUNT,
    GET_WELDING_COUNT_RESPONSE,
    REQUEST_RESOURCE_REQUEST,
    REQUEST_RESOURCE_RESPONSE,

}