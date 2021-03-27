package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateCoordinator(context: StateMachineContext): State {
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    // The coordinator chooses the cycle for welding
    override fun entry(context: StateMachineContext) {
        // Get welding count of every robot
        val robots = context.robot.getSortedRobotList()

        println("[${context.robot.id}]: WeldingCounts:")
        robots.forEach { println(it.weldingCount) }
        println(context.robot.weldingCount)

        // Select robots with smallest welding count
        // and add self to cycle
        val cycle = listOf(context.robot.id, robots[0].id, robots[1].id)

        context.currentState = StateCoordinatorWelding(context, cycle)
        context.currentState.entry(context)
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
        context.currentState.entry(context)
    }
}
