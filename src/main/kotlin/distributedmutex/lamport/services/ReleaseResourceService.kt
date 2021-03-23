package distributedmutex.lamport.services

import distributedmutex.lamport.LamportMutex
import distributedmutex.lamport.Request
import middleware.Message
import middleware.MessageType
import middleware.Service

class ReleaseResourceService(private var lamport: LamportMutex): Service {
    override fun call(m: Message): Any {
        val req = m.contents

        if (req is Request) {
            lamport.removeRequests(req.id)
        } else {
            error("ReleaseResourceService: Message contents ${m.contents} received has wrong format")
        }

        return Message(MessageType.ACK, "OK")
    }
}
