package robot

import lamport.Request
import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.Robot

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

//    fun requestResource(): Message {
//       val req = Message(MessageType.REQUEST_RESOURCE, Request(robot.id, System.nanoTime()))
//       return call(req)
//    }
}