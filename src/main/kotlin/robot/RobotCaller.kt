package robot

import middleware.Message
import middleware.MessageType
import middleware.Stub

class RobotCaller(
    uri: String,
    port: Int,
    private val robot: Robot
): Stub(uri, port) {

    fun coordinator(id: Int): Message {
        val req = Message(MessageType.COORDINATOR, id)
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

    fun welding(participants: List<Int>): Message {
        val req = Message(MessageType.WELDING, participants)
        return call(req)
    }

    fun weldingSuccessful(): Message {
        val req = Message(MessageType.WELDING_DONE, robot.id)
        return call(req)
    }

    fun robotFailure(): Message {
        val req = Message(MessageType.ROBOT_FAILURE, robot.id)
        return call(req)
    }
}
