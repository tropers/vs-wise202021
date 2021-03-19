package robot.statemachine.states

import robot.statemachine.StateMachineContext

class StateCoordinatorWelding(context: StateMachineContext): State {
    init {

    }

    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}