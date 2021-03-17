package robot

class Robot(var id: Int) {
    var weldingCount: Int = 0
    var participants: MutableList<Robot> = mutableListOf()

}
