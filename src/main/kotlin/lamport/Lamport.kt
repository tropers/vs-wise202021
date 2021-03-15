package lamport

data class Request(var id: Int, var timestamp: Long)

class Lamport() {
    var requestList: MutableList<Request> = mutableListOf()
}