package de.duckulus.floppa

import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.TextMessage


fun MessageInfo.text(): String {
    val content = message().content()
    if (content !is TextMessage) return ""
    return content.text()
}