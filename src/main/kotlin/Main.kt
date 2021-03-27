import distributedmutex.lamport.LamportMutex
import distributedmutex.lamport.services.ReleaseResourceService
import distributedmutex.lamport.services.RequestResourceService
import middleware.MessageType
import middleware.Skeleton
import middleware.services.*
import robot.Robot
import java.io.IOException
import java.net.ServerSocket


fun portAvailable(port: Int): Boolean {
    var portFree: Boolean
    try {
        ServerSocket(port).use { ignored -> portFree = true }
    } catch (e: IOException) {
        portFree = false
    }
    return portFree
}

fun getFreePort(portRange: IntRange): Int {
    for (p in portRange) {
        if (portAvailable(p))
            return p
    }

    error("No port within $portRange free")
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("usage: robot [ID] [PORT_RANGE_MIN] [PORT_RANGE_MAX]")
    } else {
        val robot = Robot(args[0].toInt()) // Create robot object

        val portRange = args[1].toInt()..args[2].toInt()

        println("[${args[0]}]: Getting free port for server...")
        val port = getFreePort(portRange)

        val skeleton = Skeleton(port) // Create server skeleton

        skeleton.registerService(MessageType.GET_WELDING_COUNT, GetWeldingCountService(robot))
        skeleton.registerService(MessageType.REGISTER_REQUEST, RegisterService(robot))
        skeleton.registerService(MessageType.COORDINATOR, SetCoordinatorService(robot))
        skeleton.registerService(MessageType.WELDING_READY, WeldingReadyService(robot))
        skeleton.registerService(MessageType.WELDING_DONE, WeldingDoneService(robot))
        skeleton.registerService(MessageType.WELDING, WeldingService(robot))
        skeleton.registerService(MessageType.ROBOT_FAILURE, RobotFailureService(robot))
        skeleton.registerService(MessageType.ELECTION, ElectionService(robot))
        skeleton.registerService(MessageType.REQUEST_RESOURCE, RequestResourceService(robot.distributedMutex as LamportMutex))
        skeleton.registerService(MessageType.RELEASE_RESOURCE, ReleaseResourceService(robot.distributedMutex as LamportMutex))

        println("[${args[0]}]: Starting up server on $port")
        // Run skeleton server
        val server = Thread(skeleton)
        server.start()

        Thread.sleep(1000)

        println("[${args[0]}]: Registering ${robot.id} in the system...")
//        // Register robot in network
//        robot.register(port, portRange)

        var previousRegistered = robot.participants.size

        robot.register(port, portRange)

        while (previousRegistered != robot.participants.size || previousRegistered < 1) {
            // Register robot in network
            robot.register(port, portRange)
            previousRegistered = robot.participants.size

            // Wait for at least two other robots
//            robot.registerCountdownLatch.await()
        }

        println("[${robot.id}]: Registered participants: ")
        robot.participants.forEach { println(it.key) }

        println("[${args[0]}]: Enough robots in system, starting experiment!")
        robot.stateMachine.currentState.election(robot.stateMachine)
    }
}
