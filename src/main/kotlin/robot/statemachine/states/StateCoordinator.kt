package robot.statemachine.states

import robot.statemachine.StateMachineContext
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock

class StateCoordinator(context: StateMachineContext): State {
    init {
        chooseCycle(context)
    }

    // The coordinator chooses the cycle for welding
    private fun chooseCycle(context: StateMachineContext) {
        // Get welding count of every robot
        val robots = context.robot.getSortedRobotList()

        // Select robots with smallest welding count
        // and add self to cycle
        val cycle = listOf(context.robot.id, robots[0].id, robots[1].id)

        context.currentState = StateCoordinatorWelding(context, cycle)
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
