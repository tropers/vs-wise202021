package robot.statemachine.states

import middleware.Message
import robot.statemachine.StateMachineContext
import java.io.IOException

/**
 * StateElection implements the bully algorithm to determine
 * the coordinator of the current setup of nodes
 */
class StateElection(context: StateMachineContext): State {
    init {
        doElection(context)
    }

    private fun doElection(context: StateMachineContext) {
        // Bully-Algorithm for electing the coordinator
        // Send election message to all higherups
        for ((k, v) in context.robot.participants) {
            if (context.robot.id < k) {
                try {
                    val m = context.robot.robotCallers[k]?.election()
                    if (m is Message && m.contents is String && m.contents == "OK") {
                        context.currentState = StateIdle(context)
                        return // Abort if someone responded (did not win election)
                    }
                } catch (e: IOException) {
                    println("Robot $k not reachable")
                }
            }
        }

        context.robot.currentCoordinator = context.robot

        // Send victory message
        for ((k, v) in context.robot.robotCallers) {
            v.coordinator()
        }

        context.currentState = StateCoordinator(context)
    }

    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
