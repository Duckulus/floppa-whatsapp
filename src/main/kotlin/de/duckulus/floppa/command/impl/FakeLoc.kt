package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.contact.ContactJid
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.LocationMessage

object FakeLoc : Command("fakeloc", "sends a fake location to a chat") {
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        if (args.size < 3) {
            whatsapp.sendMessage(
                messageInfo.chat(), "Not enough arguments. Usage: fakeloc <chat> <latitude> <longitude>"
            )
            return
        }
        var jid: ContactJid
        val chat = whatsapp.store().findChatByName(args[0])
        val contact = whatsapp.store().findContactByName(args[0])
        if (chat.isPresent) {
            jid = chat.get().jid()
        } else if (contact.isPresent) {
            jid = contact.get().jid()
        } else {
            whatsapp.sendMessage(
                messageInfo.chat(), "Chat not found"
            )
            return
        }

        val location =
            LocationMessage.builder().live(true).latitude(args[1].toDouble()).longitude(args[2].toDouble()).build()
        whatsapp.sendMessage(jid, location)
    }
}