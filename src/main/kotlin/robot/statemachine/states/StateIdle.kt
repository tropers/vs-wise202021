package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateIdle(context: StateMachineContext): State {
    override fun welding(context: StateMachineContext, participants: List<Int>) {
        context.currentState = StateWelding(context, participants)
    }

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
