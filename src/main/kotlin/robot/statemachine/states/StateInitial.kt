package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateInitial(context: StateMachineContext): State{
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    override fun entry(context: StateMachineContext) {}

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {
        context.currentState = StateElection(context)
        context.currentState.entry(context)
    }

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
