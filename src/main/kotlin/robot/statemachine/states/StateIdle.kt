package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateIdle(context: StateMachineContext): State {
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    override fun entry(context: StateMachineContext) {}

    override fun welding(context: StateMachineContext, cycle: List<Int>) {
        context.currentState = StateWelding(context, cycle)
        context.currentState.entry(context)
    }

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
