import middleware.MessageType
import middleware.Skeleton
import middleware.services.*
import robot.Robot
import java.io.IOException
import java.net.Socket

fun portAvailable(port: Int): Boolean {
    try {
        Socket("localhost", port).use { ignored -> return false }
    } catch (ignored: IOException) {
        return true
    }
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
        val robot = Robot(args[1].toInt()) // Create robot object

        val portRange = args[2].toInt()..args[3].toInt()

        val port = getFreePort(portRange)

        val serv = Skeleton(port) // Create server skeleton

        serv.registerService(MessageType.GET_WELDING_COUNT, GetWeldingCountService(robot))
        serv.registerService(MessageType.REGISTER_REQUEST, RegisterService(robot))
        serv.registerService(MessageType.COORDINATOR, SetCoordinatorService(robot))
        serv.registerService(MessageType.WELDING_DONE, WeldingDoneService(robot))
        serv.registerService(MessageType.WELDING, WeldingService(robot))
        serv.registerService(MessageType.ROBOT_FAILURE, RobotFailureService(robot))

        // Register robot in network
        robot.register(portRange)

        while (robot.participants.size < 2)
            continue

        // Run skeleton server
        serv.run()
    }
}
