package io.dotrun.mcvotifierlib

import java.net.InetSocketAddress

abstract class VotifierClient(open val address: InetSocketAddress) {
    abstract fun sendVote(vote: Vote)
}
