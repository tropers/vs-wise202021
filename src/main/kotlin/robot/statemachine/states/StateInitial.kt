package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateInitial(context: StateMachineContext): State{

    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {
        context.currentState = StateElection()
    }

    override fun coordinator(context: StateMachineContext) {}
}