package robot.statemachine.states

import middleware.Message
import robot.statemachine.StateMachineContext

class StateCoordinator(context: StateMachineContext): State {
    init {
        chooseCycle(context)
    }

    // The coordinator chooses the cycle for welding
    private fun chooseCycle(context: StateMachineContext) {
        // Get welding count of every robot
        for ((k, v) in context.robot.robotCallers) {
            val wc = v.weldingCount().contents

            if (wc is Int) {
                context.robot.participants[k]?.weldingCount = wc
            }
        }

        // Sort all robots by weldingcount
        val robots = context.robot.participants.toList()
        robots.sortedByDescending{context.robot.weldingCount}

        // Select robots with smallest welding count
        val cycle = listOf(robots[0].second.id, robots[1].second.id)
        for ((_, v) in context.robot.robotCallers) {
            v.welding()
        }
    }

    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}
}
