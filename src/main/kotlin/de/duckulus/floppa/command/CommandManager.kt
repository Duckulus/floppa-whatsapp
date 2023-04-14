package de.duckulus.floppa.command

import de.duckulus.floppa.command.impl.PingCommand
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.TextMessage

object CommandManager {
    private const val prefix = ";"
    val commands = HashMap<String, Command>()

    fun registerCommands() {
        PingCommand
    }

    fun handleCommand(whatsapp: Whatsapp, messageInfo: MessageInfo) {
        val content = messageInfo.message().content()
        if (content !is TextMessage) return
        if (!content.text().startsWith(prefix)) return
        val words = content.text().split(" ")
        val commandName = words[0].substring(prefix.length)
        val args = words.subList(1, words.size).toTypedArray()

        println("Executing $commandName with args ${args.contentToString()}")

        commands[commandName]?.execute(whatsapp, messageInfo, args)
    }
}