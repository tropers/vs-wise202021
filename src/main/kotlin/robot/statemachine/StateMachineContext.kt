package robot.statemachine

import distributedmutex.IDistributedMutex
import distributedmutex.lamport.LamportMutex
import middleware.Stub
import robot.Robot
import robot.statemachine.states.State
import robot.statemachine.states.StateInitial
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.withLock

const val WELDING_ROBOTS_AMOUNT = 3

class StateMachineContext(
    var robot: Robot,
) {
    var currentState: State = StateInitial(this)

    // Used by coordinator to check if all robots have welded successfully
    // in current cycle
    var weldingCountDownLatch: CountDownLatch = CountDownLatch(WELDING_ROBOTS_AMOUNT)
}
