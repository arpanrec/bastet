package com.arpanrec.minerva.physical

import kotlinx.serialization.Serializable

@Serializable
data class KeyValue(
    var key: String? = null,
    var value: String? = null,
    var metadata: Map<String, String> = mapOf(),
    var version: Int = 0,
    var isBinary: Boolean = false,
    var keyValueBinary: KeyValueBinary? = null
)
