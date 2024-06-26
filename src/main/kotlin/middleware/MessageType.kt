package middleware

import java.io.Serializable

/**
 * MessageType designates the different messages exchanged between services
 */
enum class MessageType: Serializable {
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    ELECTION,
    ELECTION_RESPONSE,
    COORDINATOR,
    COORDINATOR_RESPONSE,
    GET_WELDING_COUNT,
    GET_WELDING_COUNT_RESPONSE,
    WELDING,
    WELDING_READY,
    WELDING_READY_ACK,
    WELDING_NOT_READY_ACK,
    WELDING_DONE,
    REQUEST_RESOURCE,
    REQUEST_RESPONSE_ACK,
    RELEASE_RESOURCE,
    ROBOT_FAILURE,
    LOG,
    ACK,
    ERR
}
