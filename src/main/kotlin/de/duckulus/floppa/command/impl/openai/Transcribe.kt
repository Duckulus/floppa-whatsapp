package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import de.duckulus.floppa.command.Command
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.AudioMessage
import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.TimeUnit

const val inFileName = "in.ogg"
const val outFileName = "out.mp3"

object Transcribe : Command("transcribe", "transcribes a voice message") {

    @OptIn(BetaOpenAI::class)
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        val quoted = messageInfo.quotedMessage()
        if (quoted.isEmpty) {
            whatsapp.sendMessage(messageInfo.chat(), "Please reply to a voice message")
            return
        }
        val content = quoted.get().message().content()
        if (content !is AudioMessage) {
            whatsapp.sendMessage(messageInfo.chat(), "This command only works with voice messages")
            return
        }
        runBlocking {
            if (content.duration() > 30) {
                whatsapp.sendMessage(messageInfo.chat(), "Audio too long")
                return@runBlocking
            }
            if (content.decodedMedia().isEmpty) {
                whatsapp.sendMessage(messageInfo.chat(), "Message is empty")
                return@runBlocking
            }
            val audio: ByteArray
            if (content.voiceMessage()) {
                val inFile = File(inFileName)
                FileUtils.writeByteArrayToFile(inFile, content.decodedMedia().get())
                ProcessBuilder("ffmpeg -i $inFileName -acodec libmp3lame -y $outFileName".split(" "))
                    .directory(File("./"))
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor(30, TimeUnit.SECONDS)
                val outFile = File(outFileName)
                if (!outFile.exists()) {
                    whatsapp.sendMessage(messageInfo.chat(), "Error")
                    return@runBlocking
                }
                audio = File(outFileName).readBytes()
            } else {
                audio = content.decodedMedia().get()
            }
            try {
                val result = openAi.transcription(
                    TranscriptionRequest(
                        audio = FileSource("test.mp3", Buffer().write(audio)),
                        model = ModelId("whisper-1")
                    )
                )
                whatsapp.sendMessage(messageInfo.chat(), "Transcription: ${result.text}")
            } catch (exception: OpenAIException) {
                whatsapp.sendMessage(messageInfo.chat(), "Error: ${exception.message}")
            }
        }
    }

}