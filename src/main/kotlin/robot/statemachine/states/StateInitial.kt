package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateInitial(context: StateMachineContext): State{

    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {
        context.currentState = StateElection(context)
    }

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
