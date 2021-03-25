package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.RegisterRequest
import middleware.Service
import robot.Robot
import robot.RobotCaller
import kotlin.concurrent.withLock

class RegisterService(private var robot: Robot): Service {
    override fun call(m: Message): Message {
        val r = m.contents

        if (r is RegisterRequest) {
            robot.participantsLock.withLock {
                val robot = Robot(r.id)
                this.robot.participants[r.id] = robot
                this.robot.robotCallers[r.id] = RobotCaller(r.uri, r.port, this.robot)
            }
        } else {
            error("Register failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.REGISTER_RESPONSE, robot.id)
    }
}
