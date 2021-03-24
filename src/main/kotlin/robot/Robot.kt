package robot

import distributedmutex.IDistributedMutex
import distributedmutex.lamport.LamportMutex
import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.statemachine.StateMachineContext
import robot.statemachine.states.StateError
import robot.statemachine.states.StateIdle
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.random.Random

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableMap<Int, Robot> = mutableMapOf()
    var robotCallers: MutableMap<Int, RobotCaller> = mutableMapOf()
    var currentCoordinator: Robot? = null
    var stateMachine: StateMachineContext = StateMachineContext(this) // Use the Lamport Mutex

    // Lock for accessing the participants list and robotCallers
    var participantsLock = ReentrantLock()

    // Distributed Lock used for welding
    private var distributedMutex: IDistributedMutex = LamportMutex(id) // Use Lamport

    private var weldingLock = ReentrantLock()
    private var weldingSignal = weldingLock.newCondition()

    // Get stubs of robots participating in a welding process
    fun getStubs(participants: List<Int>): List<Stub> {
        val stubs = mutableListOf<Stub>()

        participantsLock.withLock {
            for (p in participants) {
                if (p in robotCallers.keys)
                // Add RobotCaller to stubs list if not null
                    robotCallers[p]?.let { stubs.add(it) }
            }
        }

        return stubs
    }

    // Get list of robots sorted by weldingcount
    fun getSortedRobotList(): List<Robot> {
        var robots = mutableListOf<Robot>()

        participantsLock.withLock {
            for ((k, v) in robotCallers) {
                val wc = v.weldingCount().contents

                if (wc is Int) {
                    participants[k]?.weldingCount = wc
                }
            }

            // Sort all robots by weldingcount
            robots = participants.toList().map { it.second } as MutableList<Robot>
            robots.sortedBy { weldingCount }
        }

        return robots
    }

    // Registers the robot in the network
    fun register(portRange: IntRange) {
        for (port in portRange) {
            try {
                val stub = Stub("localhost", port) // TODO: configurable IP
                val registerReq = Message(MessageType.REGISTER_REQUEST, this)

                val res = stub.call(registerReq)

                val robot = res.contents
                if (res.type == MessageType.REGISTER_RESPONSE && robot is Robot) {
                    participantsLock.withLock {
                        participants[robot.id] = robot
                        robotCallers[robot.id] = RobotCaller("localhost", port, robot)
                    }
                } else {
                    error("RegisterError: Response has wrong type $robot")
                }

            } catch(e: IOException) { /* If no connection can be made, skip */ }
        }
    }

    // TODO: Probably have to change this (parameters not listed in assignment)
    fun welding(stubs: List<Stub>): Boolean {
        // Acquire the distributed mutex
        distributedMutex.acquire(stubs)

        Thread.sleep(1000) // TODO: make configurable

        if (Random.nextInt(0, 100) > 1) { // 99% chance
            var ack: Message?

            participantsLock.withLock {
                ack = robotCallers[currentCoordinator?.id]?.weldingSuccessful()
            }

            if (ack?.type != MessageType.ACK) {
                // TODO: What to do with mutex in error state (possibly release like below)
                distributedMutex.release(stubs)
                return false
            } else {
                // Increase weldingcount
                ++weldingCount

                // Release the distributed mutex
                distributedMutex.release(stubs)
                return true
            }
        } else {
            return false
        }
    }

    fun setStatus() {} // TODO
}
