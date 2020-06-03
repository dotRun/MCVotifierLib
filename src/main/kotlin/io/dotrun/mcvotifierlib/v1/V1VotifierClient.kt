package io.dotrun.mcvotifierlib.v1

import io.dotrun.mcvotifierlib.Vote
import io.dotrun.mcvotifierlib.VotifierClient
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher

data class V1VotifierClient(
    override val address: InetSocketAddress,
    val publicKey: RSAPublicKey
) : VotifierClient(address) {
    private val rsa = Cipher.getInstance("RSA").also { it.init(Cipher.ENCRYPT_MODE, publicKey) }

    override fun sendVote(vote: Vote) {
        Socket(address.address, address.port).use { socket ->
            val out = DataOutputStream(socket.getOutputStream())
            val block = rsa.doFinal(vote.toString().toByteArray())

            out.write(block)
            out.close()
        }
    }
}
