package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import io.github.cdimascio.dotenv.Dotenv
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.api.WhatsappOptions.WebOptions
import it.auties.whatsapp.controller.DefaultControllerSerializer
import it.auties.whatsapp.listener.OnLoggedIn
import java.nio.file.Path
import kotlin.system.exitProcess

val env: Dotenv = Dotenv.configure().ignoreIfMissing().load()

val JDOODLE_CLIENT_ID: String = env["JDOODLE_CLIENT_ID"]
val JDOODLE_CLIENT_SECRET: String = env["JDOODLE_CLIENT_SECRET"]

fun main() {
    if (JDOODLE_CLIENT_ID.isEmpty() || JDOODLE_CLIENT_SECRET.isEmpty()) {
        println("Missing environment variables")
        exitProcess(1)
    }
    CommandManager.registerCommands()

    val serializer = DefaultControllerSerializer(Path.of("./session"))
    val api = Whatsapp.lastConnection(
        WebOptions.builder()
            .serializer(serializer)
            .deserializer(serializer)
            .build()
    )
    api.addLoggedInListener(OnLoggedIn {
        println("Hello Whatsapp")
    })
    api.connect().join()
}