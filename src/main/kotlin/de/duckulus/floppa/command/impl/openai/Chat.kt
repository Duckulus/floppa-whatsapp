package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import de.duckulus.floppa.command.Command
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.AudioMessage
import it.auties.whatsapp.model.message.standard.TextMessage
import kotlinx.coroutines.runBlocking

object Chat : Command("chat", "generates text with gpt-3.5-turbo") {

    @OptIn(BetaOpenAI::class)
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        if (args.isEmpty()) {
            whatsapp.sendMessage(messageInfo.chat(), "Please provide a prompt")
            return
        }
        runBlocking {
            var prompt = ""
            if (!messageInfo.quotedMessage().isEmpty) {
                val content = messageInfo.quotedMessage().get().message().content()
                prompt = when (content) {
                    is TextMessage -> content.text()
                    is AudioMessage -> transcribeMessage(content)
                    else -> ""
                } + "\n\n"
            }
            prompt += args.joinToString(" ")

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