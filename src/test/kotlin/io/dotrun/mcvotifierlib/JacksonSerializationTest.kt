package io.dotrun.mcvotifierlib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.node.ObjectNode
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

class JacksonSerializationTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `vote serializes to a tree with the challenge appended`() {
        val vote = Vote(serviceName = "TestService", username = "player1", address = "127.0.0.1", timestamp = "12345")

        val tree = mapper.valueToTree<ObjectNode>(vote)
        tree.put("challenge", "abc123")

        assertEquals("TestService", tree["serviceName"].asString())
        assertEquals("player1", tree["username"].asString())
        assertEquals("127.0.0.1", tree["address"].asString())
        assertEquals("12345", tree["timestamp"].asString())
        assertEquals("abc123", tree["challenge"].asString())
    }

    @Test
    fun `v2 message round-trips through bytes`() {
        val message = V2VoteSender.V2Message(signature = "sig", payload = "{}")

        val bytes = mapper.writeValueAsBytes(message)
        val decoded = mapper.readValue<V2VoteSender.V2Message>(String(bytes))

        assertEquals(message, decoded)
    }

    @Test
    fun `v2 response deserializes from json`() {
        val json = """{"status":"ok","cause":null,"error":null}"""

        val response = mapper.readValue<V2VoteSender.V2Response>(json)

        assertEquals("ok", response.status)
        assertEquals(null, response.cause)
        assertEquals(null, response.error)
    }
}
