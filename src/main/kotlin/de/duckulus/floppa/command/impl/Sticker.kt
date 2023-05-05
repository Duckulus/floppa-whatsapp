package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.ImageMessage
import it.auties.whatsapp.model.message.standard.StickerMessage
import nu.pattern.OpenCV
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfInt
import org.opencv.imgcodecs.Imgcodecs

fun encodeToWebP(image: ByteArray): ByteArray {
    val matImage = Imgcodecs.imdecode(MatOfByte(*image), Imgcodecs.IMREAD_UNCHANGED)
    val parameters = MatOfInt(Imgcodecs.IMWRITE_WEBP_QUALITY, 100)
    val output = MatOfByte()
    if (Imgcodecs.imencode(".webp", matImage, output, parameters)) {
        return output.toArray()
    } else {
        throw IllegalStateException("Failed to encode the image as webp")
    }
}

object Sticker : Command("sticker", "Create a Sticker from an image", PermissionLevel.USER) {

    init {
        OpenCV.loadShared()
    }

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        val message = messageInfo.quotedMessage()
        if (message.isEmpty) {
            whatsapp.sendMessage(messageInfo.chat(), "You need to reply to a message")
            return
        }

        val content = message.get().message().content()
        if (content !is ImageMessage) {
            whatsapp.sendMessage(messageInfo.chat(), "Please reply to an image message")
            return
        }

        val decodedMedia = content.decodedMedia()
        if (decodedMedia.isEmpty) {
            whatsapp.sendMessage(messageInfo.chat(), "Failed to decode image")
            return
        }

        val webpImage = encodeToWebP(decodedMedia.get())
        val webpThumbnail = encodeToWebP(content.thumbnail())

        val stickerMessage = StickerMessage.simpleBuilder()
            .media(webpImage)
            .thumbnail(webpThumbnail)
            .animated(false)
            .build()
        stickerMessage.width(content.width())
        stickerMessage.height(content.height())
        
        whatsapp.sendMessage(messageInfo.chat(), stickerMessage)
    }

}