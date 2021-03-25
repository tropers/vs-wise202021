package middleware

import java.io.Serializable

data class Message(var type: MessageType, var contents: Any): Serializable
