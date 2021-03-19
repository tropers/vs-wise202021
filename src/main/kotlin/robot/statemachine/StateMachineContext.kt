package robot.statemachine

import robot.Robot
import robot.statemachine.states.State

class StateMachineContext(var robot: Robot) {
    var currentState: State? = null
}
