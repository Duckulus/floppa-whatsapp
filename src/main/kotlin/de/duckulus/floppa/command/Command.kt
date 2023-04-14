package de.duckulus.floppa.command

import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

abstract class Command(
    name: String,
    val description: String
) {
    init {
        CommandManager.commands[name] = this
    }

    abstract fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>)
}