package distributedmutex.lamport

import distributedmutex.IDistributedMutex
import middleware.Message
import middleware.MessageType
import middleware.Stub
import java.io.Serializable
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Request(var id: Int, var timestamp: Long): Serializable

class LamportMutex(var id: Int): IDistributedMutex {
    private var requestList: MutableList<Request> = mutableListOf()
    private val requestListLock = ReentrantLock()
    private val requestListLockCondition = requestListLock.newCondition()

    // Lock used for waiting
    private val acquireLock = ReentrantLock()
    private val acquireLockCondition = acquireLock.newCondition()

    fun addRequest(req: Request) {
        requestListLock.withLock {
            requestList.add(req)
        }
    }

    private fun removeRequest(req: Request) {
        requestListLock.withLock {
            requestList.remove(req)
        }
    }

    fun removeRequests(id: Int) {
        requestListLock.withLock {
            requestList.removeAll { it.id == id}

            // Signal any waiting resource acquisition
            requestListLockCondition.signalAll()
        }
    }

    private fun checkTimestamps(): Boolean {
        val myRequest = requestList.find { it.id == id } ?: error("No request with own ID found in requestList")

        for (r in requestList) {
            if (id == r.id) continue

            if ((myRequest.timestamp < r.timestamp && r.id != id) // If timestamps are equal, check IDs for ordering
                || (myRequest.timestamp == r.timestamp && r.id > id)) { // If other process has higher id than self, weld after
                return false
            }
        }

        return true
    }

    override fun acquire(stubs: List<Stub>) {
        val req = Request(id, System.currentTimeMillis())
        val msg = Message(MessageType.REQUEST_RESOURCE, req)

        addRequest(req)

        // Send request to other participants
        for (s in stubs) {
            val res = s.call(msg)

            if (res.type != MessageType.REQUEST_RESOURCE_RESPONSE) {
                error("LamportMutex: Wrong message type received ${res.type}")
            }

            // Add response from other participants to requests-queue
            val resContent = res.contents
            if (resContent is Request) {
//                addRequest(resContent)
            } else {
                error("LamportMutex: Content of response $res is not Request type")
            }
        }

        requestListLock.withLock {
            while (!checkTimestamps())
                requestListLockCondition.await()
        }
    }

    override fun release(stubs: List<Stub>) {
        val req = Request(id, System.currentTimeMillis())
        val msg = Message(MessageType.RELEASE_RESOURCE, req)

        // Remove all requests of self from requests-queue
        removeRequests(id)

        // Signal other participants
        for (s in stubs) {
            val res = s.call(msg)

            if (res.type != MessageType.ACK) {
                error("LamportMutex: Wrong message type received in release ${res.type}")
            }
        }
    }
}
