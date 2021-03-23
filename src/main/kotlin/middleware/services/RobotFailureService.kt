package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot
import kotlin.concurrent.withLock

class RobotFailureService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        val robotId = m.contents

        if (m.type == MessageType.ERR && robotId is Int) {
            robot.participantsLock.withLock {
                robot.participants.remove(m.contents)
                robot.robotCallers.remove(m.contents)
            }

            return Message(MessageType.ACK, "OK")
        } else {
            error("RobotFailureService: Wrong message received $m")
        }
    }
}
