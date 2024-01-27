package com.arpanrec.minerva.physical

import jakarta.annotation.PostConstruct
import java.nio.file.Path
import java.util.Optional
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext

@Serializable
data class KeyValue(
    var key: String? = null, var value: String? = null, var isBinary: Boolean = false, var metadata: Map<String, String> = mapOf(), var version: Int = 0
)

interface KeyValueStorage {
    fun save(keyValue: KeyValue): KeyValue
    fun update(keyValue: KeyValue): KeyValue
    fun get(key: String, version: Int): Optional<KeyValue>
    fun delete(keyValue: KeyValue): KeyValue
    fun getNextVersion(keyPath: Path): Int
    fun getLatestVersion(keyPath: Path): Int
}

@ConfigurationProperties(prefix = "minerva.key-value")
class KeyValuePersistence(private var persistenceType: KeyValuePersistenceType) {

    @Autowired
    private var applicationContext: ApplicationContext? = null

    private var keyValueStorage: KeyValueStorage? = null

    @PostConstruct
    fun setKeyValueStorage() {
        keyValueStorage = when (persistenceType) {
            KeyValuePersistenceType.FILE -> {
                applicationContext!!.getBean(KeyValueFileStorage::class.java)
            }
        }
    }

    fun get(key: String, version: Int): Optional<KeyValue> {
        return keyValueStorage!!.get(key = key, version = version)
    }

    fun save(keyValue: KeyValue): KeyValue {
        return keyValueStorage!!.save(keyValue)
    }
}

enum class KeyValuePersistenceType {
    FILE
}
