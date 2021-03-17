package lamport.services

import lamport.Lamport
import lamport.Request
import middleware.Message
import middleware.MessageType
import middleware.Service

class RequestResourceService(private var lamport: Lamport): Service {
    override fun call(m: Message): Any {
        val r = m.contents

        if (r is Request) {
            lamport.requestList.add(r)
        } else {
            error("Requesting resource failed: Wrong type of message contents ${m.contents}")
        }

        return Message(MessageType.REQUEST_RESOURCE_RESPONSE, "OK")
    }
}
