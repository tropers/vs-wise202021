package middleware.services

import middleware.Message
import middleware.Service
import robot.Robot

class RegisterService(private var robot:Robot): Service {
    override fun call(m: Message): Message {
        if (m.contents is Robot) {
            robot.participants.add(m.contents as Robot)
        } else {
            error("Register failed: Wrong type of message contents ${m.contents}")
        }

        return Message("register_response", robot)
    }
}
