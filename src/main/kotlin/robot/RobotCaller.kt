package robot

import middleware.Message
import middleware.MessageType
import middleware.Stub

class RobotCaller(
    uri: String,
    port: Int,
    private val robot: Robot
): Stub(uri, port) {

    fun coordinator(): Message {
        val req = Message(MessageType.COORDINATOR, robot.id)
        return call(req)
    }

    fun election(): Message {
        val req = Message(MessageType.ELECTION, robot.id)
        return call(req)
    }

    fun weldingCount(): Message {
        val req = Message(MessageType.GET_WELDING_COUNT, 0)
        return call(req)
    }

    fun welding(): Message {
        val req = Message(MessageType.WELDING, 0)
        return call(req)
    }

    fun weldingSuccessful(): Message {
        val req = Message(MessageType.WELDING_DONE, 0)
        return call(req)
    }

    fun systemFailure(): Message {
        val req = Message(MessageType.ERR, "system_failure")
        return call(req)
    }
}
