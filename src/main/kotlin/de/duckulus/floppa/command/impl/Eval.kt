package de.duckulus.floppa.command.impl

import de.duckulus.floppa.command.Command
import de.duckulus.floppa.http.jdoodle.eval
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo
import kotlinx.coroutines.runBlocking

object Eval : Command("eval", "executes a piece of code") {
    override fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>) {
        if (args.size < 2) {
            whatsapp.sendMessage(messageInfo.chat(), "Not enough arguments. Usage: eval <language> <code>")
            return
        }
        runBlocking {
            val resp = eval(args[0], args.copyOfRange(1, args.size).joinToString(" "))
            if (resp.error != null) {
                whatsapp.sendMessage(messageInfo.chat(), "Language not available")
                return@runBlocking
            }
            whatsapp.sendMessage(messageInfo.chat(), "Program output:\n ${resp.output}")
        }
    }
}