package distributedmutex.lamport

import distributedmutex.IDistributedMutex
import middleware.Message
import middleware.MessageType
import middleware.Stub
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Request(var id: Int, var timestamp: Long)

class LamportMutex(var id: Int, var stub: MutableMap<Int, Stub>): IDistributedMutex {
    private var requestList: MutableList<Request> = mutableListOf()
    private val requestListLock = ReentrantLock()

    fun addRequest(req: Request) {
        requestListLock.withLock {
            requestList.add(req)
        }
    }

    override fun acquire(stubs: List<Stub>) {
        val req = Request(id, System.nanoTime())
        val msg = Message(MessageType.REQUEST_RESOURCE, req)

        addRequest(req)

        for (s in stubs) {
            val res = s.call(msg)

            if (res.type != MessageType.REQUEST_RESOURCE_RESPONSE) {
                error("LamportMutex: Wrong message type received ${res.type}")
            }

            val resContent = res.contents
            if (resContent is Request) {
                addRequest(resContent)
            } else {
                error("LamportMutex: Content of response $res is not Request type")
            }
        }
    }

    override fun release(stubs: List<Stub>) {
        TODO("Not yet implemented")
    }
}
