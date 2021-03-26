package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class SetCoordinatorService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        val id = m.contents

        if (id is Int) {
            if (id == robot.id) { // Is coordinator
                println("[${robot.id}]: Got elected new coordinator!")
                robot.currentCoordinator = robot

                Thread {
                    robot.stateMachine.currentState.coordinator(robot.stateMachine)
                }.start()
            } else {
                robot.currentCoordinator = robot.participants[id]
            }
        } else {
            error("SetCoordinator failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.COORDINATOR_RESPONSE, "OK")
    }
}
