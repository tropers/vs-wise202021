package robot.statemachine.states

import robot.statemachine.StateMachineContext

interface State {
    fun welding(context: StateMachineContext, participants: List<Int>)
    fun election(context: StateMachineContext)
    fun coordinator(context: StateMachineContext)
    fun systemFailure(context: StateMachineContext)
}
