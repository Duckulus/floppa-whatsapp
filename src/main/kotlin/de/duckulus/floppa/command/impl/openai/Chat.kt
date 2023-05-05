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
import it.auties.whatsapp.model.contact.ContactJid
import it.auties.whatsapp.model.info.MessageInfo
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ArrayBlockingQueue

const val contextSize = 10

object Chat : Command("chat", "generates text with gpt-3.5-turbo", PermissionLevel.ADMIN) {

    private val mutex = Mutex()

    @OptIn(BetaOpenAI::class)
    private val contexts = mutableMapOf<ContactJid, ArrayBlockingQueue<ChatMessage>>()

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

            val contextList: MutableList<ChatMessage>

            mutex.withLock {
                val context = if (contexts[messageInfo.chatJid()] == null) {
                    ArrayBlockingQueue<ChatMessage>(contextSize)
                } else {
                    contexts[messageInfo.chatJid()]!!
                }

                contextList = context.toMutableList()
                contextList.add(ChatMessage(ChatRole.User, prompt))
                println(contextList.toTypedArray().contentToString())
            }


            val reply = openAi.chatCompletion(
                ChatCompletionRequest(
                    messages = contextList,
                    model = ModelId("gpt-3.5-turbo")
                )
            )
            whatsapp.sendMessage(messageInfo.chat(), reply.choices.first().message?.content ?: "No response :(")

            mutex.withLock {
                val context = if (contexts[messageInfo.chatJid()] == null) {
                    ArrayBlockingQueue<ChatMessage>(contextSize)
                } else {
                    contexts[messageInfo.chatJid()]!!
                }

                if (!context.offer(ChatMessage(ChatRole.User, prompt))) {
                    context.poll()
                    context.offer(ChatMessage(ChatRole.User, prompt))
                }

                if (!context.offer(ChatMessage(ChatRole.Assistant, reply.choices.first().message?.content ?: ""))) {
                    context.poll()
                    context.offer(ChatMessage(ChatRole.Assistant, reply.choices.first().message?.content ?: ""))
                }

                contexts[messageInfo.chatJid()] = context
            }
        }

    }

}