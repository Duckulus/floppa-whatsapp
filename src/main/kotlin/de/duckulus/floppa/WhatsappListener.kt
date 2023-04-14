package de.duckulus.floppa

import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.listener.Listener
import it.auties.whatsapp.listener.RegisterListener
import it.auties.whatsapp.model.info.MessageInfo
import it.auties.whatsapp.model.message.standard.TextMessage

@RegisterListener
data class WhatsappListener(val whatsapp: Whatsapp) : Listener {

    override fun onNewMessage(info: MessageInfo) {
        val content = info.message().content()
        if (content !is TextMessage) {
            return
        }
        if (!whatsapp.store().userCompanionJid().toPhoneNumber().equals(info.sender().get().jid().toPhoneNumber())) {
            return
        }
        println(content.text())
    }
}