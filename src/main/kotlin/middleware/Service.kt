package middleware

interface Service {
    fun call(m: Message): Any
}
