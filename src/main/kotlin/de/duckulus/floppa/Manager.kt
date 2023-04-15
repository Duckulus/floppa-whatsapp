package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import io.github.cdimascio.dotenv.Dotenv
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.listener.OnLoggedIn
import kotlin.system.exitProcess

val env: Dotenv = Dotenv.load()

val JDOODLE_CLIENT_ID = env["JDOODLE_CLIENT_ID"]
val JDOODLE_CLIENT_SECRET = env["JDOODLE_CLIENT_SECRET"]

fun main() {
    if (JDOODLE_CLIENT_ID == null || JDOODLE_CLIENT_SECRET == null) {
        println("Missing environment variables")
        exitProcess(1)
    }
    CommandManager.registerCommands()

    val api = Whatsapp.firstConnection()
    api.addLoggedInListener(OnLoggedIn {
        println("Hello Whatsapp")
    })
    api.connect().join()
}