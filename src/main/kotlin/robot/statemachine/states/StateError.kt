package robot.statemachine.states

import robot.statemachine.StateMachineContext
import kotlin.concurrent.withLock

class StateError(context: StateMachineContext): State {
    init {
        // Tell everyone robot has failed
        context.robot.participantsLock.withLock {
            for ((_, v) in context.robot.robotCallers) {
                v.robotFailure()
            }
        }
    }

    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {}
}