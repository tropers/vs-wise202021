import middleware.MessageType
import middleware.Skeleton
import middleware.services.*
import robot.Robot

fun main(args: Array<String>) {
    println("Hello, World!")

    if (args.size < 2) {
        println("usage: robot [ID] [PORT]")
    } else {
        val robot = Robot(args[1].toInt()) // Create robot object

        val portRange = 50050..50066
        val serv = Skeleton(args[2].toInt()) // Create server skeleton

        serv.registerService(MessageType.GET_WELDING_COUNT, GetWeldingCountService(robot))
        serv.registerService(MessageType.REGISTER_REQUEST, RegisterService(robot))
        serv.registerService(MessageType.COORDINATOR, SetCoordinatorService(robot))
        serv.registerService(MessageType.WELDING_DONE, WeldingDoneService(robot))
        serv.registerService(MessageType.WELDING, WeldingService(robot))

        serv.run()
    }
}
