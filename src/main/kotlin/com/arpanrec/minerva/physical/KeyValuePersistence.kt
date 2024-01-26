package com.arpanrec.minerva.physical

import kotlinx.serialization.Serializable
import java.nio.file.Path
import java.util.Optional

@Serializable
data class KeyValue(
    var key: String? = null,
    var value: String? = null,
    var isBinary: Boolean = false,
    var metadata: Map<String, String> = mapOf(),
    var version: Int = 0
)

interface KeyValueStorage {
    fun save(keyValue: KeyValue): KeyValue
    fun update(keyValue: KeyValue): KeyValue
    fun get(key: String, version: Int): Optional<KeyValue>
    fun delete(keyValue: KeyValue): KeyValue
    fun getNextVersion(keyPath: Path): Int
    fun getLatestVersion(keyPath: Path): Int
}

class KeyValuePersistence {

    private val keyValueStorage: KeyValueStorage = KeyValueFileStorage()

    fun get(key: String, version: Int): Optional<KeyValue> {
        return keyValueStorage.get(key = key, version = version)
    }

    fun save(keyValue: KeyValue): KeyValue {
        return keyValueStorage.save(keyValue)
    }
}
