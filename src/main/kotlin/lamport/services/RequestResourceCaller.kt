package lamport.services

import lamport.Request
import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.Robot

class RequestResourceCaller(
    private var uri: String,
    private var port: Int,
    private var robot: Robot
): Stub(uri, port) {

    fun requestResource(): Message {
        val req = Message(MessageType.REQUEST_RESOURCE, Request(robot.id, System.nanoTime()))

        val res = call(req)
        if (res is Message) {
            return res
        } else {
            error("Requesting resource failed: Response is not a message")
        }
    }
}
