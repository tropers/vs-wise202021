package robot.statemachine.states

import robot.statemachine.StateMachineContext

interface State {
    fun entry(context: StateMachineContext)
    fun welding(context: StateMachineContext, cycle: List<Int>)
    fun election(context: StateMachineContext)
    fun coordinator(context: StateMachineContext)
    fun systemFailure(context: StateMachineContext)
}
