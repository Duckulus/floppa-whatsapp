package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import de.duckulus.floppa.db.DB
import io.github.cdimascio.dotenv.Dotenv
import io.github.oshai.KotlinLogging
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.controller.DefaultControllerSerializer
import it.auties.whatsapp.listener.OnLoggedIn
import kotlin.io.path.Path

val env: Dotenv = Dotenv.configure().ignoreIfMissing().load()

val JDOODLE_CLIENT_ID: String = env["JDOODLE_CLIENT_ID"]
val JDOODLE_CLIENT_SECRET: String = env["JDOODLE_CLIENT_SECRET"]
val OPENAI_API_KEY: String = env["OPENAI_API_KEY"]

val logger = KotlinLogging.logger("Floppa")

fun main() {
    CommandManager.registerCommands()
    DB
    DB.createTables()

    val serializer = DefaultControllerSerializer(Path("./session"))
    val api = Whatsapp.webBuilder().serializer(serializer).lastConnection().build()
    api.addLoggedInListener(OnLoggedIn {
        logger.info("Hello Whatsapp")
    })
    api.registerFloppaHandler()
    api.connect().join()
}