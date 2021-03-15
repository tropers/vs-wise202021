package lamport

data class Request(var id: Int, var timestamp: Long)

class Lamport {
    private var requestList: MutableList<Request>? = null

    fun requestResource(id: Int) {

    }
}