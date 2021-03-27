package logger.services

import middleware.Message
import middleware.MessageType
import middleware.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime

class LoggingService(private val logPath: String): Service {
    override fun call(m: Message): Any {
        val log = m.contents
        if (log is String) {
            val logFile = File(logPath)
            val writer = FileWriter(logPath, true)

            writer.write("${LocalDateTime.now()}: ${m.contents}\n")

            writer.close()
        }

        return Message(MessageType.ACK, "OK")
    }
}