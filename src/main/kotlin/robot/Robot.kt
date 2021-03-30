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


/* Robot Status */
const val STATUS_WORKING = 1
const val STATUS_OK = 0
const val STATUS_NOK = -1

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableMap<Int, Robot> = mutableMapOf()
    var robotCallers: MutableMap<Int, RobotCaller> = mutableMapOf()
    var currentCoordinator: Robot? = null
    var stateMachine: StateMachineContext = StateMachineContext(this) // Use the Lamport Mutex
    var robotStatus: Int = 0

    var logger: LoggerCaller? = null

    // Lock for accessing the participants list and robotCallers
    var participantsLock = ReentrantLock()

    // Distributed Lock used for welding
    var distributedMutex: IDistributedMutex = LamportMutex(id) // Use Lamport

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
        }

        return robots.sortedBy { it.weldingCount }
    }

    // Registers the robot in the network
    fun register(ownPort: Int, portRange: IntRange) {
        for (port in portRange) {
            if (port != ownPort) {
                try {
                    logger?.log("[${id}]: Registering at $port")
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
                    } else {
                        error("RegisterError: Response has wrong type $registerRes")
                    }
                } catch (e: IOException) {
                    logger?.log("[$id]: No robot reached at ${port}, skipping...")
                } /* If no connection can be made, skip */
            }
        }
    }

    fun welding(stubs: List<Stub>): Boolean {
        logger?.log("[$id]: Preparing to weld...")
        // Acquire the distributed mutex
        distributedMutex.acquire(stubs)
        logger?.log("[$id]: welding...")
        setStatus(STATUS_WORKING)

        Thread.sleep(2000) // TODO: make configurable

        if (Random.nextInt(0, 100) >= 1) { // 99% chance
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
                distributedMutex.release(stubs)
                setStatus(STATUS_NOK)
                return false
            } else {
                // Increase weldingcount
                ++weldingCount

                // Release the distributed mutex
                distributedMutex.release(stubs)
                setStatus(STATUS_OK)
                return true
            }
        } else {
            setStatus(STATUS_NOK)
            return false
        }
    }

    fun setStatus(status: Int) {
        robotStatus = status
    }
}
