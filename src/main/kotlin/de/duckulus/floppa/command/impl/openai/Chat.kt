package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Emojy
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import kotlinx.coroutines.runBlocking

object Chat : Command("chat", "generates text with gpt-3.5-turbo", PermissionLevel.ADMIN) {

    @OptIn(BetaOpenAI::class)
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        runBlocking {
            whatsapp.sendReaction(messageInfo, Emojy.COUNTERCLOCKWISE_ARROWS_BUTTON)
            val quoted = ctx.quotedText
            var prompt = if (quoted.isEmpty) "" else quoted.get() + "\n\n"
            prompt += args.joinToString(" ")

            if (prompt.isEmpty()) {
                whatsapp.sendMessage(messageInfo.chat(), "Please provide a prompt or reply to a message")
                return@runBlocking
            }

            val reply = openAi.chatCompletion(
                ChatCompletionRequest(
                    messages = listOf(ChatMessage(ChatRole.User, prompt)),
                    model = ModelId("gpt-3.5-turbo")
                )
            )
            whatsapp.sendMessage(messageInfo.chat(), reply.choices.first().message?.content ?: "No response :(")
        }

    }

}