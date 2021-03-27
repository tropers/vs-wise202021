package robot

import middleware.Message
import middleware.MessageType
import middleware.Stub

class LoggerCaller(port: Int): Stub("localhost", port) {
    fun log(logText: String): Message {
        println(logText)
        return call(Message(MessageType.LOG, logText))
    }
}
