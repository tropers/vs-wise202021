package robot.statemachine.states

import robot.Robot
import robot.statemachine.StateMachineContext

class StateCoordinatorWelding(context: StateMachineContext, var cycle: List<Int>): State {
    init {

    }

    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}