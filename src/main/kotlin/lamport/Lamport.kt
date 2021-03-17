package lamport

data class Request(var id: Int, var timestamp: Long)

class Lamport(private var id: Int) {
    var requestList: MutableList<Request> = mutableListOf()
}
