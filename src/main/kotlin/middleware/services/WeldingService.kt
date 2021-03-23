package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class WeldingService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        val participants = m.contents

        if (participants is List<*>) {
            if (participants.all { it is Int }) {
                robot.stateMachine.currentState.welding(robot.stateMachine, participants as List<Int>)
                return Message(MessageType.ACK, "OK")
            } else {
                error("WeldingService: Participants list received has wrong type (not int) $participants")
            }
        }

        return Message(MessageType.ERR, "ERROR")
    }
}
