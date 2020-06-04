package io.dotrun.mcvotifierlib

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.security.Key
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class V2VoteSender(
    override val address: InetSocketAddress,
    val key: Key
) : VoteSender(address) {

    companion object {
        const val MAGIC = 0x733A
    }

    data class V2Message(val signature: String, val payload: String)
    data class V2Response(val status: String, val cause: String?, val error: String?)

    constructor(address: InetSocketAddress, token: String) : this(
        address,
        SecretKeySpec(token.toByteArray(), "HmacSHA256")
    )

    private val mapper = jacksonObjectMapper()
    private val mac = Mac.getInstance("HmacSHA256").also { it.init(key) }

    override fun sendVote(vote: Vote) {
        Socket(address.address, address.port).use { socket ->
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val out = DataOutputStream(socket.getOutputStream())

            // read challenge
            val challenge = reader.readLine().split(' ').let {
                check(it.size == 3) { "Greeting does not include challenge, not a v2 server!" }
                it[2]
            }

            // construct and send vote
            val payload: String = mapper.valueToTree<ObjectNode>(vote).let {
                it.put("challenge", challenge)
                it.toString()
            }
            val signature = Base64.getEncoder().encodeToString(mac.doFinal(payload.toByteArray()))
            val message = mapper.writeValueAsBytes(
                V2Message(
                    signature,
                    payload
                )
            )
            out.writeShort(MAGIC)
            out.writeShort(message.size)
            out.write(message)

            // read response
            val response = mapper.readValue<V2Response>(reader.readText())
            check(response.status == "ok") { "Vote failed! '${response.cause}: ${response.error}'" }

            reader.close()
            out.close()
        }
    }
}
