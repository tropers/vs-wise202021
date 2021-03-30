package robot.statemachine.states

import robot.STATUS_NOK
import robot.statemachine.StateMachineContext
import kotlin.concurrent.withLock

class StateError(context: StateMachineContext): State {
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
        context.robot.setStatus(STATUS_NOK)
    }

    override fun entry(context: StateMachineContext) {
        // Tell everyone robot has failed
        context.robot.participantsLock.withLock {
            for ((_, v) in context.robot.robotCallers) {
                v.robotFailure()
            }
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {}
}