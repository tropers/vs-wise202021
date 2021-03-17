package middleware

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

open class Stub(private var uri: String, private var port: Int) {
    fun call(m: Message): Any {
        val socket = Socket(uri, port)

        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(m)

        val inputStream = ObjectInputStream(socket.getInputStream())

        return inputStream.readObject() as Message
    }
}