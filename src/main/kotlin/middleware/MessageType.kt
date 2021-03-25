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
    WELDING_DONE,
    REQUEST_RESOURCE,
    REQUEST_RESOURCE_RESPONSE,
    RELEASE_RESOURCE,
    ROBOT_FAILURE,
    ACK,
    ERR
}
