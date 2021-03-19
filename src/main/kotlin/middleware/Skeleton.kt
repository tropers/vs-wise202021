package middleware

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

class Skeleton(private var port: Int): Runnable {
    private var running = true
    private var requestQueue: BlockingQueue<Socket> = LinkedBlockingDeque<Socket>()
    private var services: MutableMap<MessageType, Service> = mutableMapOf<MessageType, Service>()

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

    fun registerService(msgType: MessageType, serv: Service) {
        services[msgType] = serv
    }

    override fun run() {
        val server = ServerSocket(port) // Server socket listening for new connections

        Thread{handleRequests()}.start()

        while (running) {
            val socket = server.accept()
            requestQueue.put(socket)
        }
    }
}
