package de.duckulus.floppa.command

import io.github.oshai.KotlinLogging
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.info.MessageInfo

abstract class Command(
    name: String,
    val description: String,
    val minimumPermissionLevel: PermissionLevel = PermissionLevel.ADMIN
) {
    private val logger = KotlinLogging.logger(name)

    init {
        CommandManager.commands[name] = this
        logger.info("Registering $name command")
    }

    abstract fun execute(whatsapp: Whatsapp, messageInfo: MessageInfo, args: Array<String>)
}