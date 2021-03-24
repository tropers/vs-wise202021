package robot.statemachine.states

import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.statemachine.StateMachineContext
import kotlin.concurrent.withLock
import kotlin.random.Random

class StateWelding(context: StateMachineContext, private var cycle: List<Int>): State {
    init {
        doWeld(context)
    }

    private fun doWeld(context: StateMachineContext) {
        val stubs = context.robot.getStubs(cycle)

        // If welding successful, go into idle state, if not error
        if (context.robot.welding(stubs)) {
            context.currentState = StateIdle(context)
        } else {
            context.currentState = StateError(context)
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
