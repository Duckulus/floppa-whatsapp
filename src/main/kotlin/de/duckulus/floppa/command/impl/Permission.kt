package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import de.duckulus.floppa.db.DB
import de.duckulus.floppa.getPermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

object Permission : Command("permission", "Read and Write the permission level of a user", PermissionLevel.USER) {

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        if (args.isEmpty()) {
            val permissionLevel = getPermissionLevel(whatsapp, messageInfo.senderJid())
            whatsapp.sendMessage(messageInfo.chat(), "Your permission level is ${permissionLevel.name}")
            return
        } else if (ctx.permissionLevel.isAtLeast(PermissionLevel.OP)) {
            if (!args[0].startsWith("@")) {
                whatsapp.sendMessage(messageInfo.chat(), "Please specify a user")
                return
            }
            val phoneNumber = args[0].replace("@", "+")
            if (args.size == 1) {
                val user = DB.getPermissionLevel(phoneNumber)
                whatsapp.sendMessage(
                    messageInfo.chat(),
                    "The permission level of $phoneNumber is ${PermissionLevel.values()[user].name}"
                )
            } else if (args.size == 2) {
                val newPermissionLevel = PermissionLevel.values().find { it.name.equals(args[1], true) }
                if (newPermissionLevel == null) {
                    whatsapp.sendMessage(
                        messageInfo.chat(),
                        "Please specify a valid permission level: [${PermissionLevel.values().joinToString(", ")}]"
                    )
                    return
                }
                if (newPermissionLevel == PermissionLevel.OP) {
                    whatsapp.sendMessage(messageInfo.chat(), "You can't set a user to OP")
                    return
                }
                DB.setPermissionLevel(phoneNumber, newPermissionLevel.ordinal)
                whatsapp.sendMessage(
                    messageInfo.chat(),
                    "The permission level of $phoneNumber is now ${newPermissionLevel.name}"
                )
            }
        }
    }

}