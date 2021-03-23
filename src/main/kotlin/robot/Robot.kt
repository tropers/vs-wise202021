package robot

import distributedmutex.lamport.LamportMutex
import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.statemachine.StateMachineContext
import java.io.IOException

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableMap<Int, Robot> = mutableMapOf()
    var robotCallers: MutableMap<Int, RobotCaller> = mutableMapOf()
    var currentCoordinator: Robot? = null
    var stateMachine: StateMachineContext = StateMachineContext(this, LamportMutex(id)) // Use the Lamport Mutex

    // Registers the robot in the network
    fun register(portRange: IntRange) {
        for (port in portRange) {
            try {
                val stub = Stub("localhost", port) // TODO: configurable IP
                val registerReq = Message(MessageType.REGISTER_REQUEST, this)

                val res = stub.call(registerReq)

                val robot = res.contents
                if (res.type == MessageType.REGISTER_RESPONSE && robot is Robot) {
                    participants[robot.id] = robot
                    robotCallers[robot.id] = RobotCaller("localhost", port, robot)
                } else {
                    error("RegisterError: Response has wrong type $robot")
                }

            } catch(e: IOException) { /* If no connection can be made, skip */ }
        }
    }
}
