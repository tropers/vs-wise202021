package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class RegisterService(private var robot: Robot): Service {
    override fun call(m: Message): Message {
        val r = m.contents

        if (r is Robot) {
            robot.participants[r.id] = r
        } else {
            error("Register failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.REGISTER_RESPONSE, robot)
    }
}
