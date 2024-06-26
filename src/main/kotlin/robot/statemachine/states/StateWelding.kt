package robot.statemachine.states

import robot.statemachine.StateMachineContext
import kotlin.concurrent.withLock

class StateWelding(context: StateMachineContext, private var cycle: List<Int>): State {
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    override fun entry(context: StateMachineContext) {
        val stubs = context.robot.getStubs(cycle)

        // If welding successful, go into idle state, if not error
        if (context.robot.welding(stubs)) {
            context.currentState = StateIdle(context)
            context.currentState.entry(context)
        } else {
            context.currentState = StateError(context)
            context.currentState.entry(context)
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {
        context.currentState = StateCoordinator(context)
        context.currentState.entry(context)
    }

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
        context.currentState.entry(context)
    }
}
