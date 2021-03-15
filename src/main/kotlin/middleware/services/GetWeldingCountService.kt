package middleware.services

import middleware.Message
import middleware.Service
import robot.Robot

class GetWeldingCountService(private var robot: Robot): Service {
    override fun call(m: Message): Message {
        return Message("welding_count", robot.weldingCount)
    }
}
