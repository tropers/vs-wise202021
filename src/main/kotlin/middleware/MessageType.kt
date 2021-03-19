package middleware

/**
 * MessageType designates the different messages exchanged between services
 */
enum class MessageType {
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    ELECTION,
    ELECTION_RESPONSE,
    COORDINATOR,
    COORDINATOR_RESPONSE,
    GET_WELDING_COUNT,
    GET_WELDING_COUNT_RESPONSE,
    WELDING,
    WELDING_DONE,
    WELDING_RESPONSE,
    ACK,
    ERR
//    REQUEST_RESOURCE,
//    REQUEST_RESOURCE_RESPONSE,
}
