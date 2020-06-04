package io.dotrun.mcvotifierlib

import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

data class V1VoteSender(
    override val address: InetSocketAddress,
    val publicKey: RSAPublicKey
) : VoteSender(address) {

    constructor(address: InetSocketAddress, publicKey: ByteArray) : this(
        address,
        KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))) as RSAPublicKey
    )

    constructor(address: InetSocketAddress, publicKey: String) : this(
        address,
        KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))) as RSAPublicKey
    )

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
