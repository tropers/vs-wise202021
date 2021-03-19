package robot.statemachine.states

import middleware.MessageType
import robot.statemachine.StateMachineContext
import kotlin.random.Random

class StateWelding(context: StateMachineContext): State {
    init {
        doWeld(context)
    }

    private fun doWeld(context: StateMachineContext) {
        Thread.sleep(1000) // TODO: make configuraable

        if (Random.nextInt(0, 100) > 1) { // 99% chance
            val ack = context.robot.robotCallers[context.robot.currentCoordinator?.id]?.weldingSuccessful()
            if (ack?.type != MessageType.ACK){
                context.currentState = StateError(context)
            } else {
                context.currentState = StateIdle(context)
            }
        } else {
            context.currentState = StateError(context)
        }
    }

    override fun welding(context: StateMachineContext) {}

    override fun election(context: StateMachineContext) {}

    override fun coordinator(context: StateMachineContext) {}
}
