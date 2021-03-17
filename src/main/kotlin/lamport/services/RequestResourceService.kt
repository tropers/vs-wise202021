package lamport.services

import lamport.Lamport
import lamport.Request
import middleware.Message
import middleware.Service

class RequestResourceService(private var lamport: Lamport): Service {
    override fun call(m: Message): Any {
        if (m.contents is Request) {
            lamport.requestList.add(m.contents as Request)
        } else {
            error("Requesting resource failed: Wrong type of message contents ${m.contents}")
        }

        return Message("resource_ack", "OK")
    }
}
