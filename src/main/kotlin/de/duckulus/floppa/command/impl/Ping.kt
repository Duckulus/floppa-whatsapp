package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

object Ping : Command("ping", "pongs") {

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        whatsapp.sendMessage(messageInfo.chat(), "pong")
    }

}