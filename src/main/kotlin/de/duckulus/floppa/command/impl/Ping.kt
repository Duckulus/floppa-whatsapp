package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

object Ping : Command("ping", "pongs", PermissionLevel.USER) {

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        whatsapp.sendMessage(messageInfo.chat(), "pong")
    }

}