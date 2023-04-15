package de.duckulus.floppa.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
        })
    }
}