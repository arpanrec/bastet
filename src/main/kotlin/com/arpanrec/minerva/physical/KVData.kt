package com.arpanrec.minerva.physical

import kotlinx.serialization.Serializable

@Serializable
data class KVData(
    val value: String, val metadata: Map<String, String>
)
