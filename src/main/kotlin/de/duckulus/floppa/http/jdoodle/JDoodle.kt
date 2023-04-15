package de.duckulus.floppa.http.jdoodle

import de.duckulus.floppa.JDOODLE_CLIENT_ID
import de.duckulus.floppa.JDOODLE_CLIENT_SECRET
import de.duckulus.floppa.http.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

suspend fun eval(language: String, script: String): JDoodleApiResponse = runBlocking {
    return@runBlocking ktorClient.post("https://api.jdoodle.com/v1/execute") {
        contentType(ContentType.Application.Json)
        setBody(
            JDoodleApiRequest(
                clientId = JDOODLE_CLIENT_ID,
                clientSecret = JDOODLE_CLIENT_SECRET,
                script = script,
                language = language,
                compileonly = false
            )
        )
    }.body()
}