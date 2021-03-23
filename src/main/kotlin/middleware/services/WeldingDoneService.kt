package middleware.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import robot.Robot

class WeldingDoneService(private var robot: Robot): Service {
    override fun call(m: Message): Any {
        if (m.type == MessageType.WELDING_DONE) {
            robot.stateMachine.weldingCountDownLatch.countDown()
            return Message(MessageType.ACK, "OK")
        } else {
            error("WeldingDoneService: Wrong message type received ${m.type}")
        }
    }
}
