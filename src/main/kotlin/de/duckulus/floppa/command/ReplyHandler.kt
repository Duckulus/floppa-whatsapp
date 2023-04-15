package de.duckulus.floppa.command

import it.auties.whatsapp.model.info.MessageInfo

object ReplyHandler {

    private val pendingReplies = mutableMapOf<String, (MessageInfo) -> Unit>()

    fun MessageInfo.waitForReply(timeout: Long, handler: (MessageInfo) -> Unit) {
        pendingReplies[id()] = handler
    }

    fun handlePossibleReply(messageInfo: MessageInfo) {
        val quoted = messageInfo.quotedMessage()
        if (quoted.isEmpty) return
        pendingReplies[quoted.get().id()]?.invoke(messageInfo)
    }

}