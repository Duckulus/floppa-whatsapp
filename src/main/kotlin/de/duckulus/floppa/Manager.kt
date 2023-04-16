package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.util.*
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.api.WhatsappOptions.WebOptions
import it.auties.whatsapp.controller.DefaultControllerSerializer
import it.auties.whatsapp.listener.OnLoggedIn
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder
import java.nio.file.Path

val env: Dotenv = Dotenv.configure().ignoreIfMissing().load()

val JDOODLE_CLIENT_ID: String = env["JDOODLE_CLIENT_ID"]
val JDOODLE_CLIENT_SECRET: String = env["JDOODLE_CLIENT_SECRET"]
val OPENAI_API_KEY: String = env["OPENAI_API_KEY"]
val DISCORD_WEBHOOK_URL: String = env["DISCORD_WEBHOOK_URL"]

@OptIn(InternalAPI::class)
fun main() {
    CommandManager.registerCommands()

    val serializer = DefaultControllerSerializer(Path.of("./session"))
    val api = Whatsapp.lastConnection(
        WebOptions.builder()
            .serializer(serializer)
            .deserializer(serializer)
            .qrHandler {
                runBlocking {
                    val url = "https://quickchart.io/qr?text=${URLEncoder.encode(it, "UTF-8")}"
                    println(url)
                    sendWebhookImage(
                        DISCORD_WEBHOOK_URL,
                        url
                    )
                }
            }
            .build()
    )
    api.addLoggedInListener(OnLoggedIn {
        println("Hello Whatsapp")
    })
    api.connect().join()
}