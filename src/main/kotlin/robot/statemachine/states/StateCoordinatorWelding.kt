package robot.statemachine.states

import robot.Robot
import robot.statemachine.StateMachineContext
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock

class StateCoordinatorWelding(context: StateMachineContext, private var cycle: List<Int>): State {
    init {
        val stubs = context.robot.getStubs(cycle)

        // Call other participants to weld
        for ((_, v) in context.robot.robotCallers) {
            v.welding(cycle)
        }

        // Also weld
        var weldingSuccessful = context.robot.welding(stubs)

        if (!context.weldingCountDownLatch.await(4, TimeUnit.SECONDS)) { // TODO: make configurable
            context.robot.participantsLock.withLock {
                for ((k, v) in context.robot.robotCallers) {
                    v.robotFailure()
                }
            }
            context.currentState = StateError(context)
        } else {
            // Choose new coordinator
            val robots = context.robot.getSortedRobotList()

            var i = 0
            var newCoordinator = robots[i]
            while (newCoordinator.id == context.robot.id)
                newCoordinator = robots[++i]

            // Set new coordinator
            context.robot.currentCoordinator = newCoordinator

            context.robot.participantsLock.withLock {
                for ((_, v)in context.robot.robotCallers) {
                    v.coordinator(newCoordinator.id)
                }
            }

            context.currentState = StateIdle(context)
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
