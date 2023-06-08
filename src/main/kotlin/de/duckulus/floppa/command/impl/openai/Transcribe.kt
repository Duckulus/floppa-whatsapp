package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Emoji
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.AudioMessage
import okio.Buffer
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.TimeUnit

const val inFileName = "in.ogg"
const val outFileName = "out.mp3"

@OptIn(BetaOpenAI::class)
suspend fun transcribeMessage(message: AudioMessage): String {
    val audio: ByteArray
    if (message.voiceMessage()) {
        val inFile = File(inFileName)
        FileUtils.writeByteArrayToFile(inFile, message.decodedMedia().get())
        ProcessBuilder("ffmpeg -i $inFileName -acodec libmp3lame -y $outFileName".split(" "))
            .directory(File("./"))
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(30, TimeUnit.SECONDS)
        val outFile = File(outFileName)
        if (!outFile.exists()) {
            throw IllegalStateException("ffmpeg failed to convert file")
        }
        audio = File(outFileName).readBytes()
    } else {
        audio = message.decodedMedia().get()
    }

    val result = openAi.transcription(
        TranscriptionRequest(
            audio = FileSource("test.mp3", Buffer().write(audio)),
            model = ModelId("whisper-1")
        )
    )
    return result.text
}

object Transcribe : Command("transcribe", "transcribes a voice message", PermissionLevel.ADMIN) {

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        val quoted = messageInfo.quotedMessage()
        if (quoted.isEmpty) {
            whatsapp.sendMessage(messageInfo.chat(), "Please reply to a voice message")
            return
        }
        val content = quoted.get().message().content()
        val result = ctx.quotedText
        if (content !is AudioMessage || result.isEmpty) {
            whatsapp.sendMessage(messageInfo.chat(), "This command only works with voice messages")
            return
        }
        whatsapp.sendReaction(messageInfo, Emoji.COUNTERCLOCKWISE_ARROWS_BUTTON)
        whatsapp.sendMessage(messageInfo.chat(), "Transcription: ${result.get()}")
    }

}