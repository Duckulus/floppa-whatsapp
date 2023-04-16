package de.duckulus.floppa.command

import de.duckulus.floppa.command.impl.FakeLoc
import de.duckulus.floppa.command.impl.Ping
import de.duckulus.floppa.command.impl.eval.Eval
import de.duckulus.floppa.command.impl.openai.Chat
import de.duckulus.floppa.command.impl.openai.Image
import de.duckulus.floppa.command.impl.openai.Transcribe
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.TextMessage

object CommandManager {
    private const val prefix = "."
    val commands = HashMap<String, Command>()

    fun registerCommands() {
        Ping
        FakeLoc
        Eval
        Chat
        Image
        Transcribe
    }

    fun handleCommand(whatsapp: Whatsapp, messageInfo: MessageInfo) {
        val content = messageInfo.message().content()
        if (content !is TextMessage) return
        if (!content.text().startsWith(prefix)) return
        val words = content.text().split(" ")
        val commandName = words[0].substring(prefix.length)
        val args = words.subList(1, words.size).toTypedArray()

        val command = commands[commandName]

        if (command != null) {
            println("Executing $commandName with args ${args.contentToString()}")
            try {
                command.execute(whatsapp, messageInfo, args)
            } catch (e: Exception) {
                whatsapp.sendMessage(messageInfo.chat(), "An Error occured while executing the command: ${e.message}")
            }

        }
    }
}