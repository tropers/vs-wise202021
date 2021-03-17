package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateIdle(context: StateMachineContext): State {
    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}
}