package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.CommandManager
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

object Help : Command("help", "Shows this message", PermissionLevel.USER) {

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        val help = "All Command:\n" + CommandManager.commands.map { (_, cmd) ->
            "${CommandManager.prefix}${cmd.name} (${cmd.minimumPermissionLevel.name})\n${cmd.description}"
        }.joinToString(separator = "\n\n")
        whatsapp.sendMessage(messageInfo.chat(), help)
    }

}