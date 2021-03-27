package distributedmutex.lamport.services

import distributedmutex.lamport.LamportMutex
import distributedmutex.lamport.Request
import middleware.Message
import middleware.MessageType
import middleware.Service

class RequestResourceService(private var lamport: LamportMutex): Service {
    override fun call(m: Message): Any {
        val r = m.contents

        if (r is Request) {
            lamport.addRequest(r)
        } else {
            error("Requesting resource failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.REQUEST_RESOURCE_RESPONSE, Request(lamport.id, 0))
    }
}
