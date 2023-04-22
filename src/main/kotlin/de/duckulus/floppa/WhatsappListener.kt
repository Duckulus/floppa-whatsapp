package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import de.duckulus.floppa.command.ReplyHandler
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.listener.OnNewMessage
import it.auties.whatsapp.model.message.standard.TextMessage

fun Whatsapp.registerFloppaHandler() {
    addNewMessageListener(OnNewMessage { info ->
        val content = info.message().content()

        if (!store().jid().toPhoneNumber().equals(info.sender().get().jid().toPhoneNumber())) {
            return@OnNewMessage
        }
        if (content !is TextMessage) {
            return@OnNewMessage
        }
        CommandManager.handleCommand(this, info)
        ReplyHandler.handlePossibleReply(info)
    })
}