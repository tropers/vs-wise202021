package robot

import distributedmutex.IDistributedMutex
import distributedmutex.lamport.LamportMutex
import middleware.*
import robot.statemachine.StateMachineContext
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.random.Random

const val MINIMUM_ROBOT_COUNT = 2

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableMap<Int, Robot> = mutableMapOf()
    var robotCallers: MutableMap<Int, RobotCaller> = mutableMapOf()
    var currentCoordinator: Robot? = null
    var stateMachine: StateMachineContext = StateMachineContext(this) // Use the Lamport Mutex

    // Lock for accessing the participants list and robotCallers
    var participantsLock = ReentrantLock()

    // Distributed Lock used for welding
    var distributedMutex: IDistributedMutex = LamportMutex(id) // Use Lamport

    var registerCountdownLatch = CountDownLatch(MINIMUM_ROBOT_COUNT)

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
    fun register(ownPort: Int, portRange: IntRange) {
        for (port in portRange) {
            if (port != ownPort) {
                try {
                    println("[${id}]: Registering at $port")
                    val stub = Stub("localhost", port)
                    val registerReq = Message(MessageType.REGISTER_REQUEST, RegisterRequest(id,  "localhost", ownPort))

                    val res = stub.call(registerReq)

                    val registerRes = res.contents
                    if (res.type == MessageType.REGISTER_RESPONSE && registerRes is RegisterResponse) {
                        participantsLock.withLock {
                            if (!participants.containsKey(registerRes.id)) {
                                val robot = Robot(registerRes.id)
                                participants[registerRes.id] = robot
                                robotCallers[registerRes.id] = RobotCaller("localhost", port, this)
                            }

                            if (registerRes.currentCoordinatorId > -1) {
                                currentCoordinator = participants[registerRes.currentCoordinatorId]
                            }
                        }

                        registerCountdownLatch.countDown()
                    } else {
                        error("RegisterError: Response has wrong type $registerRes")
                    }
                } catch (e: IOException) {
                    println("[$id]: No robot reached at ${port}, skipping...")
                } /* If no connection can be made, skip */
            }
        }
    }

    // TODO: Probably have to change this (parameters not listed in assignment)
    fun welding(stubs: List<Stub>): Boolean {
        println("[$id]: Preparing to weld...")
        // Acquire the distributed mutex
        distributedMutex.acquire(stubs)
        println("[$id]: welding...")

        Thread.sleep(1000) // TODO: make configurable

        if (Random.nextInt(0, 100) >= 0) { // 99% chance // TODO: Put back in error chance
            var ack: Message?

            participantsLock.withLock {
                if (currentCoordinator?.id == id) {
                    ack = Message(MessageType.ACK, "Coordinator called itself")
                    stateMachine.weldingCountDownLatch.countDown()
                } else {
                    ack = robotCallers[currentCoordinator?.id]?.weldingSuccessful()
                }
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
