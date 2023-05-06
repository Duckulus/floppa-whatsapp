package de.duckulus.floppa.command.impl.openai

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import de.duckulus.floppa.OPENAI_API_KEY
import kotlin.time.Duration.Companion.seconds


private val openAiConfig = OpenAIConfig(
    token = OPENAI_API_KEY,
    timeout = Timeout(socket = 60.seconds),
    logLevel = LogLevel.Info,
)

val openAi = OpenAI(openAiConfig)