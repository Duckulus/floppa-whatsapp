package de.duckulus.floppa.command

enum class PermissionLevel(private val value: Int) {
    ZERO(0), USER(1), ADMIN(2), OP(3);

    fun isAtLeast(level: PermissionLevel): Boolean {
        return this.value >= level.value
    }

}