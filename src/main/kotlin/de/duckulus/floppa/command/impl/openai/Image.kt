package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import de.duckulus.floppa.command.Command
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.ImageMessage
import kotlinx.coroutines.runBlocking
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Image : Command("image", "generates an image with DALL.E") {

    @OptIn(BetaOpenAI::class, ExperimentalEncodingApi::class)
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        if (args.isEmpty()) {
            whatsapp.sendMessage(messageInfo.chat(), "Please provide a prompt")
            return
        }
        val prompt = args.copyOfRange(1, args.size).joinToString(" ")
        runBlocking {
            val reply = openAi.imageJSON(
                ImageCreation(
                    prompt = prompt,
                    size = ImageSize("256x256")
                )
            )
            val message = ImageMessage.simpleBuilder().media(Base64.decode(reply.first().b64JSON)).build()
            whatsapp.sendMessage(messageInfo.chat(), message)
        }
    }

}