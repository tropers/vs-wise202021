package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class WeldingService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        robot.stateMachine.currentState?.welding(robot.stateMachine)
        return Message(MessageType.ACK, "OK")
    }
}
