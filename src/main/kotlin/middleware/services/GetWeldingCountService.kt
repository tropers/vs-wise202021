package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class GetWeldingCountService(private var robot: Robot): Service {
    override fun call(m: Message): Message {
        return Message(MessageType.GET_WELDING_COUNT_RESPONSE, robot.weldingCount)
    }
}
