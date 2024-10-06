package com.arpanrec.bastet.physical

import kotlinx.serialization.Serializable

@Serializable
class KVData(
    var value: String,
    var metadata: Map<String, String> = HashMap(),
)

@Serializable
class KVDataEncrypted(
    var value: String,
    var metadata: String = "",
)
