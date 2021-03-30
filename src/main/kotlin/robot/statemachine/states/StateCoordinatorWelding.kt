package robot.statemachine.states

import middleware.MessageType
import robot.Robot
import robot.RobotCaller
import robot.statemachine.StateMachineContext
import robot.statemachine.WELDING_ROBOTS_AMOUNT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock

class StateCoordinatorWelding(context: StateMachineContext, private var cycle: List<Int>): State {
    init {
        context.robot.logger?.log("[${context.robot.id}]: Entering ${this.javaClass.name}")
    }

    override fun entry(context: StateMachineContext) {
        context.weldingCountDownLatch = CountDownLatch(WELDING_ROBOTS_AMOUNT)

        val stubs = context.robot.getStubs(cycle)
        // Wait for other participants to be ready to weld
        for (s in stubs) {
            if (s is RobotCaller) {
                val ready = s.weldingReady()
                if (ready.type == MessageType.WELDING_NOT_READY_ACK) { // If not weld ready, get new cycle to check if
                    context.currentState = StateCoordinator(context)   // robot is actually available anymore
                    context.currentState.entry(context)
                    return
                }
            }
        }

        val cycleStartTime = System.currentTimeMillis()

        // Call other participants to weld
        for (s in stubs) {
            if (s is RobotCaller)
                s.welding(cycle)
        }

        var weldingSuccessful: Boolean = true
        // Also weld
        Thread {
            weldingSuccessful = context.robot.welding(stubs)
        }.start()

        if (!context.weldingCountDownLatch.await(800, TimeUnit.MILLISECONDS)) { // TODO: make configurable
            context.robot.logger?.log("[${context.robot.id}]: Cycle time exceeded!")
            context.currentState = StateError(context)
            context.currentState.entry(context)
        } else {
            val cycleEndTime = System.currentTimeMillis()
            context.robot.logger?.log("[${context.robot.id}]: Cycle at welding count: ${context.robot.weldingCount} finished with time: ${cycleEndTime - cycleStartTime}")

            context.robot.logger?.log("[${context.robot.id}]: Choosing new coordinator...")
            // Choose new coordinator
            val robots = context.robot.getSortedRobotList()

            var i = 0
            var newCoordinator = robots[i]
            while (newCoordinator.id == context.robot.id)
                newCoordinator = robots[++i]

            context.robot.logger?.log("[${context.robot.id}]: New coordinator: ${newCoordinator.id}")
            // Set new coordinator
            context.robot.currentCoordinator = newCoordinator

            context.robot.participantsLock.withLock {
                for ((_, v)in context.robot.robotCallers) {
                    v.coordinator(newCoordinator.id)
                }
            }

            if (weldingSuccessful) {
                context.currentState = StateIdle(context)
                context.currentState.entry(context)
            } else {
                context.currentState = StateError(context)
                context.currentState.entry(context)
            }
        }
    }

    override fun welding(context: StateMachineContext, cycle: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
