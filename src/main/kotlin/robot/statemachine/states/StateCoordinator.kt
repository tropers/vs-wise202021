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
        var cycle = listOf<Int>()

        // Get welding count of every robot
        context.robot.participantsLock.withLock {
            for ((k, v) in context.robot.robotCallers) {
                val wc = v.weldingCount().contents

                if (wc is Int) {
                    context.robot.participants[k]?.weldingCount = wc
                }
            }

            // Sort all robots by weldingcount
            val robots = context.robot.participants.toList()
            robots.sortedByDescending { context.robot.weldingCount }

            // Select robots with smallest welding count
            // and add self to cycle
            cycle = listOf(context.robot.id, robots[0].second.id, robots[1].second.id)
        }

        context.currentState = StateCoordinatorWelding(context, cycle)
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
