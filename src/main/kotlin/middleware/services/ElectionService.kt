package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class ElectionService(private var robot: Robot): Service {
    override fun call(m: Message): Message {
        return Message(MessageType.ELECTION_RESPONSE, "OK")
    }
}
