package middleware

import java.io.Serializable

class RegisterRequest(
    var id: Int,
    var uri: String,
    var port: Int,
): Serializable