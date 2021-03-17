package robot.statemachine.states

import robot.statemachine.StateMachineContext

interface State {
    fun welding(context: StateMachineContext)
    fun election(context: StateMachineContext)
    fun coordinator(context: StateMachineContext)
}