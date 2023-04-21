package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import io.github.cdimascio.dotenv.Dotenv
import it.auties.whatsapp.api.ConnectionType
import it.auties.whatsapp.api.WebOptionsBuilder
import it.auties.whatsapp.controller.DefaultControllerSerializer
import it.auties.whatsapp.listener.OnLoggedIn
import kotlin.io.path.Path

val env: Dotenv = Dotenv.configure().ignoreIfMissing().load()

val JDOODLE_CLIENT_ID: String = env["JDOODLE_CLIENT_ID"]
val JDOODLE_CLIENT_SECRET: String = env["JDOODLE_CLIENT_SECRET"]
val OPENAI_API_KEY: String = env["OPENAI_API_KEY"]

fun main() {
    CommandManager.registerCommands()

    val serializer = DefaultControllerSerializer(Path("./session"))
    val api = WebOptionsBuilder(null, serializer, ConnectionType.LAST).build()
    api.addLoggedInListener(OnLoggedIn {
        println("Hello Whatsapp")
    })
    api.registerFloppaHandler()
    api.connect().join()
}