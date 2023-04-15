package de.duckulus.floppa.http.jdoodle

import kotlinx.serialization.Serializable

@Serializable
data class JDoodleApiRequest(
    val clientId: String,
    val clientSecret: String,
    val script: String,
    val stdin: String? = null,
    val language: String,
    val versionIndex: String? = null,
    val compileonly: Boolean
)

@Serializable
data class JDoodleApiResponse(
    val output: String?,
    val statusCode: Int?,
    val memory: String?,
    val cpuTime: String?,
    val compilationStatus: String?,
    val error: String?
)