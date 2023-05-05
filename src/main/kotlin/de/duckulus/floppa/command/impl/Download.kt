package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.command.CommandContext
import de.duckulus.floppa.command.PermissionLevel
import it.auties.whatsapp.api.Emojy
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.VideoMessage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.concurrent.TimeUnit

const val outFileName = "out.mp4"

object Download : Command("download", "Downloads a file from the internet using yt-dlp", PermissionLevel.ADMIN) {

    private val downloadMutex = Mutex()

    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>, ctx: CommandContext) {
        if (downloadMutex.isLocked) {
            whatsapp.sendReaction(messageInfo, Emojy.COUNTERCLOCKWISE_ARROWS_BUTTON)

        }
        runBlocking {
            downloadMutex.withLock {
                val url = if (ctx.quotedText.isEmpty) args[0] else ctx.quotedText.get().split(" ")[0]
                if (url.isEmpty()) {
                    whatsapp.sendMessage(messageInfo.chat(), "No url provided")
                    return@runBlocking
                }
                val process = ProcessBuilder(
                    "yt-dlp $url -f mp4 -S \"size:100M\" -o $outFileName --force-overwrites --max-filesize 100M --no-playlist".split(
                        " "
                    )
                )
                    .directory(File("./"))
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .start()
                val result = String(process.inputStream.readAllBytes())
                println(result)
                process.waitFor(120, TimeUnit.SECONDS)
                if (!File(outFileName).exists()) {
                    whatsapp.sendMessage(messageInfo.chat(), "Download failed")
                    return@runBlocking
                }
                if (File(outFileName).length() > 100 * 1000 * 1000) {
                    whatsapp.sendMessage(messageInfo.chat(), "File too big")
                    return@runBlocking
                }
                val message = VideoMessage.simpleVideoBuilder()
                    .media(File(outFileName).readBytes())
                    .thumbnail(ByteArray(0)) // dont touch
                    .caption(result)
                    .build()
                whatsapp.sendMessage(messageInfo.chat(), message)
                File(outFileName).delete()
            }
        }
    }
}

