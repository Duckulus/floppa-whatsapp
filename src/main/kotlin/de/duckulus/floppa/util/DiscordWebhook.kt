package de.duckulus.floppa.util

import de.duckulus.floppa.http.ktorClient
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class WebhookPayload(
    val content: String,
    val embeds: List<Embed>
)

@Serializable
data class Embed(
    val image: Image,
)

@Serializable
data class Image(
    val url: String
)

suspend fun sendWebhookImage(webhookUrl: String, imageUrl: String) {
    ktorClient.post(webhookUrl) {
        contentType(ContentType.Application.Json)
        setBody(WebhookPayload("", listOf(Embed(Image(imageUrl)))))
    }
}