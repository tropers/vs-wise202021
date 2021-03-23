package robot.statemachine.states

import middleware.Message
import middleware.MessageType
import middleware.Stub
import robot.statemachine.StateMachineContext
import kotlin.concurrent.withLock
import kotlin.random.Random

class StateWelding(context: StateMachineContext, var participants: List<Int>): State {
    init {
        doWeld(context)
    }

    private fun doWeld(context: StateMachineContext) {

        val stubs = mutableListOf<Stub>()

        context.robot.participantsLock.withLock {
            for (p in participants) {
                if (p in context.robot.robotCallers.keys)
                // Add RobotCaller to stubs list if not null
                    context.robot.robotCallers[p]?.let { stubs.add(it) }
            }
        }

        // Acquire the distributed mutex
        context.distributedMutex.acquire(stubs)

        Thread {
            Thread.sleep(1000) // TODO: make configurable

            if (Random.nextInt(0, 100) > 1) { // 99% chance
                var ack: Message?

                context.robot.participantsLock.withLock {
                    ack = context.robot.robotCallers[context.robot.currentCoordinator?.id]?.weldingSuccessful()
                }

                if (ack?.type != MessageType.ACK) {
                    // TODO: What to do with mutex in error state (possibly release like below)
                    context.distributedMutex.release(stubs)
                    context.currentState = StateError(context)
                } else {
                    // Increase weldingcount
                    ++context.robot.weldingCount

                    // Release the distributed mutex
                    context.distributedMutex.release(stubs)

                    context.currentState = StateIdle(context)
                }
            } else {
                context.currentState = StateError(context)
            }
        }.start()
    }

    override fun welding(context: StateMachineContext, participants: List<Int>) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}

    override fun systemFailure(context: StateMachineContext) {
        context.currentState = StateError(context)
    }
}
