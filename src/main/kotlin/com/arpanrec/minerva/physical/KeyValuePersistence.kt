package com.arpanrec.minerva.physical

import com.arpanrec.minerva.gnupg.GnuPG
import jakarta.annotation.PostConstruct
import java.nio.file.Path
import java.util.Optional
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext

@Serializable
data class KeyValue(
    var key: String? = null,
    var value: String? = null,
    var metadata: Map<String, String> = mapOf(),
    var version: Int = 0,
    var isBinary: Boolean = false,
    var keyValueBinary: KeyValueBinary? = null
)

@Serializable
data class KeyValueBinary(var name: String? = null)

interface KeyValueStorage {
    fun save(keyValue: KeyValue): KeyValue
    fun update(keyValue: KeyValue): KeyValue
    fun get(key: String, version: Int = 0): Optional<KeyValue>
    fun delete(keyValue: KeyValue, version: Int = 0): KeyValue
    fun getNextVersion(keyPath: Path): Int
    fun getLatestVersion(keyPath: Path): Int
    fun listKeys(keyPath: Path): List<String>
}

@ConfigurationProperties(prefix = "minerva.key-value")
class KeyValuePersistence(private var persistenceType: KeyValuePersistenceType) {

    @Autowired
    private var applicationContext: ApplicationContext? = null

    @Autowired
    private var gnuPG: GnuPG? = null

    private var keyValueStorage: KeyValueStorage? = null

    var internalStorageKey: String = "internal"

    @PostConstruct
    fun setKeyValueStorage() {
        keyValueStorage = when (persistenceType) {
            KeyValuePersistenceType.FILE -> {
                applicationContext!!.getBean(KeyValueFileStorage::class.java)
            }
        }
    }

    fun get(key: String, version: Int = 0): Optional<KeyValue> {
        val keyValue = keyValueStorage!!.get(key = key, version = version)
        keyValue.ifPresent { keyValuePresent: KeyValue ->
            keyValuePresent.value = keyValuePresent.value?.let { gnuPG!!.decrypt(it) }
        }
        return keyValue
    }

    fun save(keyValue: KeyValue): KeyValue {
        keyValue.value = keyValue.value!!.let { gnuPG!!.encrypt(it) }
        return keyValueStorage!!.save(keyValue)
    }

    fun update(keyValue: KeyValue): KeyValue {
        keyValue.value = keyValue.value!!.let { gnuPG!!.encrypt(it) }
        return keyValueStorage!!.update(keyValue)
    }
}

enum class KeyValuePersistenceType {
    FILE
}
