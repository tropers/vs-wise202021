import distributedmutex.lamport.LamportMutex
import distributedmutex.lamport.services.ReleaseResourceService
import distributedmutex.lamport.services.RequestResourceService
import middleware.MessageType
import middleware.Skeleton
import middleware.services.*
import robot.LoggerCaller
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
    if (args.size < 4) {
        println("usage: robot [ID] [PORT_RANGE_MIN] [PORT_RANGE_MAX] [LOGGER_PORT]")
    } else {
        val robot = Robot(args[0].toInt()) // Create robot object

        robot.logger = LoggerCaller(args[3].toInt())

        val portRange = args[1].toInt()..args[2].toInt()

        robot.logger?.log("[${args[0]}]: Getting free port for server...")

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

        robot.logger?.log("[${args[0]}]: Starting up server on $port")
        // Run skeleton server
        val server = Thread(skeleton)
        server.start()

        Thread.sleep(1000) // Wait for server to boot up correctly

        robot.logger?.log("[${args[0]}]: Registering ${robot.id} in the system...")

        var previousRegistered = robot.participants.size

        robot.register(port, portRange)

        // As long as new robots are found or no robots have been found yet
        // Try to register in specified port range
        while (previousRegistered != robot.participants.size || previousRegistered < 1) {
            // Register robot in network
            robot.register(port, portRange)
            previousRegistered = robot.participants.size
        }

        robot.logger?.log("[${robot.id}]: Registered participants: ")
        robot.participants.forEach { println(it.key) }

        robot.logger?.log("[${args[0]}]: Enough robots in system, starting experiment!")
        robot.stateMachine.currentState.election(robot.stateMachine)
    }
}
