package middleware

import java.io.Serializable

class RegisterResponse(
    var id: Int,
    var currentCoordinatorId: Int,
): Serializable
