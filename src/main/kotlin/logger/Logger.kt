package logger

import logger.services.LoggingService
import middleware.MessageType
import middleware.Skeleton
import java.io.File
import kotlin.math.log

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("usage: logger [LISTENING_PORT] [LOG_FILE_PATH]")
    } else {
        val skeleton = Skeleton(args[0].toInt())

        val logPath = if (args.size < 2) {
            println("[Logger]: No path specified, defaulting to \"./log.txt\"")
            "./log.txt"
        } else {
            args[1]
        }

        // Delete old logFile if exists
        val logFile = File(logPath)
        if (logFile.exists()) logFile.delete()

        skeleton.registerService(MessageType.LOG, LoggingService(logPath))

        println("[Logger]: Startin up logger on ${args[0]}")
        val server = Thread(skeleton)
        server.start()
    }
}
