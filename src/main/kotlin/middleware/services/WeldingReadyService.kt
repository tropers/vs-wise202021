package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot
import robot.statemachine.states.StateIdle

class WeldingReadyService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        if (m.type == MessageType.WELDING_READY) {
            if (robot.stateMachine.currentState is StateIdle) {
                return Message(MessageType.WELDING_READY_ACK, "OK")
            }
        }

        return Message(MessageType.WELDING_NOT_READY_ACK, "NOK")
    }
}