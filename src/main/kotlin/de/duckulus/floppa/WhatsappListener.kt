package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import de.duckulus.floppa.command.PermissionLevel
import de.duckulus.floppa.command.ReplyHandler
import de.duckulus.floppa.db.DB
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.listener.OnNewMessage
import it.auties.whatsapp.model.contact.ContactJid
import it.auties.whatsapp.model.message.standard.TextMessage
import kotlinx.coroutines.runBlocking

fun Whatsapp.registerFloppaHandler() {
    addNewMessageListener(OnNewMessage  { info ->
        val content = info.message().content()
        if (content !is TextMessage) {
            return@OnNewMessage
        }
        val permissionLevel = getPermissionLevel(this, info.senderJid())
        val whatsapp = this
        runBlocking {
            CommandManager.handleCommand(whatsapp, info, permissionLevel)

        }
        ReplyHandler.handlePossibleReply(info)
    })
}

fun getPermissionLevel(whatsapp: Whatsapp, jid: ContactJid): PermissionLevel {
    if (whatsapp.store().jid().toPhoneNumber().equals(jid.toPhoneNumber())) {
        return PermissionLevel.OP
    }
    val level = DB.getPermissionLevel(jid.toPhoneNumber())
    return PermissionLevel.values()[level]
}