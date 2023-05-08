package de.duckulus.floppa.command

import de.duckulus.floppa.command.impl.*
import de.duckulus.floppa.command.impl.eval.Eval
import de.duckulus.floppa.command.impl.openai.Chat
import de.duckulus.floppa.command.impl.openai.Image
import de.duckulus.floppa.command.impl.openai.Transcribe
import de.duckulus.floppa.command.impl.openai.transcribeMessage
import io.github.oshai.KotlinLogging
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.AudioMessage
import it.auties.whatsapp.model.message.standard.TextMessage
import java.util.*

object CommandManager {

    private val logger = KotlinLogging.logger {}

    const val prefix = "."
    val commands = HashMap<String, Command>()

    fun registerCommands() {
        Ping
        FakeLoc
        Eval
        Chat
        Chat.ClearContextCommand
        Image
        Transcribe
        Sticker
        Permission
        Help
        Download
    }

    suspend fun handleCommand(whatsapp: Whatsapp, messageInfo: MessageInfo, permissionLevel: PermissionLevel) {
        val content = messageInfo.message().content()
        if (content !is TextMessage) return
        if (!content.text().startsWith(prefix)) return
        val words = content.text().split(" ")
        val commandName = words[0].substring(prefix.length)
        val args = words.subList(1, words.size).toTypedArray()

        val command = commands[commandName.lowercase()]

        if (command != null) {
            if (!permissionLevel.isAtLeast(command.minimumPermissionLevel)) {
                logger.info(
                    "User ${
                        messageInfo.senderJid().toPhoneNumber()
                    } tried to execute $commandName without permission"
                )
                return
            }
            try {
                logger.info("Executing $commandName with args ${args.contentToString()}")
                val quotedText = getQuotedText(messageInfo, permissionLevel)
                val ctx = CommandContext(quotedText, permissionLevel)
                command.execute(whatsapp, messageInfo, args, ctx)
            } catch (e: Exception) {
                logger.error(e) { "An Error occured while executing the command: ${e.message}" }
                whatsapp.sendMessage(messageInfo.chat(), "An Error occured while executing the command: ${e.message}")
            }

        }
    }

    suspend fun getQuotedText(messageInfo: MessageInfo, permissionLevel: PermissionLevel): Optional<String> {
        val quotedMessage = messageInfo.quotedMessage()
        if (quotedMessage.isEmpty) return Optional.empty()
        val content = quotedMessage.get().message().content()
        return if (content is TextMessage) {
            Optional.of(content.text())
        } else if (content is AudioMessage && permissionLevel.isAtLeast(PermissionLevel.ADMIN)) { //Only admins can transcribe audio to save money
            Optional.of(transcribeMessage(content))
        } else {
            Optional.empty()
        }
    }
}