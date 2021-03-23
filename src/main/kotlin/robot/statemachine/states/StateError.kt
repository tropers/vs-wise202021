package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateError(context: StateMachineContext): State {
    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {}
}