package robot.statemachine.states

import robot.Robot
import robot.statemachine.StateMachineContext
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock

class StateCoordinatorWelding(context: StateMachineContext, private var cycle: List<Int>): State {
    init {
        val stubs = context.getStubs(cycle)

        // Call other participants to weld
        for ((_, v) in context.robot.robotCallers) {
            v.welding(cycle)
        }

        var weldingSuccessful = false

        // Also weld
        Thread {
            weldingSuccessful = context.robot.welding(stubs)
        }.start()

        if (!context.weldingCountDownLatch.await(4, TimeUnit.SECONDS)) { // TODO: make configurable
            context.robot.participantsLock.withLock {
                for ((k, v) in context.robot.robotCallers) {
                    v.robotFailure()
                }
            }
            context.currentState = StateError(context)
        } else {

        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}