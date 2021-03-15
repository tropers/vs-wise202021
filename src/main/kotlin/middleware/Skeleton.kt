package middleware

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.collections.HashMap

class Skeleton(private var port: Int): Runnable {
    private var running = true
    private var requestQueue: BlockingQueue<Socket> = LinkedBlockingDeque<Socket>()
    private var services: MutableMap<String, Service> = mutableMapOf<String, Service>()

    private fun handleRequests() {
        while (true) {
            val socket: Socket = requestQueue.take() // Get most recent socket

            val inputStream = ObjectInputStream(socket.getInputStream())

            val msg = inputStream.readObject() as Message

            val response = services[msg.type]?.call(msg)

            val outputStream = ObjectOutputStream(socket.getOutputStream())

            outputStream.writeObject(response)
        }
    }

    fun registerService(id: String, serv: Service) {
        services[id] = serv
    }

    override fun run() {
        val server = ServerSocket(port) // Server socket listening for new connections

        Thread{handleRequests()}.start()

        while (running) {
            val socket = server.accept()
        }
    }
}
