package robot.statemachine.states

import middleware.Message
import robot.Robot
import robot.statemachine.StateMachineContext
import java.util.concurrent.TimeUnit

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
        // and add self to cycle
        val cycle = listOf(context.robot.id, robots[0].second.id, robots[1].second.id)
        for ((_, v) in context.robot.robotCallers) {
            v.welding(cycle)
        }

        if (context.weldingCountDownLatch.await(4, TimeUnit.SECONDS)) { // TODO: make configurable
            context.currentState = StateCoordinatorWelding(context, cycle)
        } else { // ERROR
            for ((k, v) in context.robot.robotCallers) {
                v.systemFailure()
            }
            context.currentState = StateError(context)
        }
    }

    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
