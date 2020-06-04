package io.dotrun.mcvotifierlib

import java.net.InetSocketAddress

abstract class VoteSender(open val address: InetSocketAddress) {
    abstract fun sendVote(vote: Vote)
}
