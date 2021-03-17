package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class SetCoordinatorService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        val id = m.contents

        if (id is Int) {
            robot.currentCoordinator = robot.participants[id]
        } else {
            error("SetCoordinator failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.COORDINATOR_RESPONSE, "OK")
    }
}
