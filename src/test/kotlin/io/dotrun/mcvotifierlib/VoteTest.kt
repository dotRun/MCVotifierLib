package io.dotrun.mcvotifierlib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VoteTest {
    @Test
    fun `toString renders the V1 votifier wire format`() {
        val vote = Vote(serviceName = "svc", username = "user", address = "1.2.3.4", timestamp = "999")

        assertEquals("VOTE\nsvc\nuser\n1.2.3.4\n999", vote.toString())
    }
}
