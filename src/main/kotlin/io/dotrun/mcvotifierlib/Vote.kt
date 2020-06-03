package io.dotrun.mcvotifierlib

data class Vote(
    val serviceName: String,
    val username: String,
    val address: String,
    val timestamp: String = System.currentTimeMillis().toString()
) {
    override fun toString() = """
        VOTE
        $serviceName
        $username
        $address
        $timestamp
    """.trimIndent()
}
