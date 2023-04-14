package de.duckulus.floppa

import de.duckulus.floppa.command.CommandManager
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.listener.OnLoggedIn
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    CommandManager.registerCommands()

    val api = Whatsapp.firstConnection()
    api.addLoggedInListener(OnLoggedIn {
        println("Hello Whatsapp")
    })
    api.connect().await()
}