package com.arpanrec.bastet.physical

import kotlinx.serialization.Serializable

@Serializable
data class KVData(
    var key: String,
    var value: String,
    var metadata: Map<String, String> = HashMap(),
)
