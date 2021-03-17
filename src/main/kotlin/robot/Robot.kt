package robot

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableMap<Int, Robot> = mutableMapOf<Int, Robot>()
    var currentCoordinator: Robot? = null
}
