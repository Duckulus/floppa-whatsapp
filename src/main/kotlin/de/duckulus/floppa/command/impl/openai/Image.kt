package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.ImageMessage
import kotlinx.coroutines.runBlocking
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Image : Command("image", "generates an image with DALL.E", PermissionLevel.ADMIN) {

    @OptIn(BetaOpenAI::class, ExperimentalEncodingApi::class)
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        runBlocking {
            val quoted = ctx.quotedText
            var prompt = if(quoted.isEmpty)  "" else quoted.get() + "\n\n"
            prompt += args.joinToString(" ")

            if(prompt.isEmpty()) {
                whatsapp.sendMessage(messageInfo.chat(), "Please provide a prompt or reply to a message")
                return@runBlocking
            }

            val reply = openAi.imageJSON(
                ImageCreation(
                    prompt = prompt,
                    size = ImageSize("256x256")
                )
            )
            reply.forEach {
                whatsapp.sendMessage(
                    messageInfo.chat(),
                    ImageMessage.simpleBuilder().media(Base64.decode(it.b64JSON)).build()
                )
            }

        }
    }

}