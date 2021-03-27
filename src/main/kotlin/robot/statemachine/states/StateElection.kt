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
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    override fun entry(context: StateMachineContext) {
        // If no coordinator has been elected in the system
        if (context.robot.currentCoordinator == null) {
            context.robot.logger?.log("[${context.robot.id}]: No coordinator set in current run, starting election.")

            // Bully-Algorithm for electing the coordinator
            // Send election message to all higherups
            for ((k, v) in context.robot.participants) {
                if (context.robot.id < k) {
                    try {
                        val m = context.robot.robotCallers[k]?.election()
                        if (m is Message && m.contents is String && m.contents == "OK") {
                            context.currentState = StateIdle(context)
                            context.currentState.entry(context)
                            return // Abort if someone responded (did not win election)
                        }
                    } catch (e: IOException) {
                        context.robot.logger?.log("Robot $k not reachable")
                    }
                }
            }

            context.robot.currentCoordinator = context.robot

            // Send victory message
            for ((_, v) in context.robot.robotCallers) {
                v.coordinator(context.robot.id)
            }

            context.currentState = StateCoordinator(context)
            context.currentState.entry(context)
        } else {
            context.robot.logger?.log("[${context.robot.id}]: Current coordinator identified as: ${context.robot.currentCoordinator!!.id}. Skipping election.")
            context.currentState = StateIdle(context)
            context.currentState.entry(context)
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
        context.currentState.entry(context)
    }
}
