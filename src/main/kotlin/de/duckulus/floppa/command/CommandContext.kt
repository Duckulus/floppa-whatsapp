package de.duckulus.floppa.command

import java.util.*

data class CommandContext(val quotedText: Optional<String>, val permissionLevel: PermissionLevel)