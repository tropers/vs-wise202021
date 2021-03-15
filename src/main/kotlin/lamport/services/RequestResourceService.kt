package lamport.services

import lamport.Lamport
import lamport.Request
import middleware.Message
import middleware.Service
import java.awt.Robot

class RequestResourceService(private var lamport: Lamport): Service {
    override fun call(m: Message): Any {
        if (m.contents is Int) {
            lamport.requestList.add(Request(m.contents as Int, System.nanoTime()))
        } else {
            error("Requesting resource failed: Wrong type of message contents ${m.contents}")
        }

        // TODO

    }
}