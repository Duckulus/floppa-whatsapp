package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.LocationMessage

object FakeLoc : Command("fakeloc", "sends a fake location", PermissionLevel.ADMIN) {
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        if (args.size < 2) {
            whatsapp.sendMessage(
                messageInfo.chat(), "Not enough arguments. Usage: fakeloc <latitude> <longitude>"
            )
            return
        }
        val location =
            LocationMessage.builder().live(true).latitude(args[0].toDouble()).longitude(args[1].toDouble()).build()
        whatsapp.sendMessage(messageInfo.chat(), location)
    }
}