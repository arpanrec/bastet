package com.arpanrec.minerva.physical

import com.arpanrec.minerva.encryption.GnuPG
import org.springframework.beans.factory.annotation.Autowired
import java.util.Optional
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class KeyValuePersistence(
    @Value("\${minerva.physical.key-value-persistence.persistence-type:FILE}")
    private val persistenceType: KeyValuePersistenceType,
    @Autowired applicationContext: ApplicationContext,
    @Autowired private val gnuPG: GnuPG
) {

    private var keyValueStorage: KeyValueStorage = when (persistenceType) {
        KeyValuePersistenceType.FILE -> {
            applicationContext.getBean(KeyValueFileStorage::class.java)
        }
    }

    var internalStorageKey: String = "internal"

    fun get(key: String, version: Int = 0): Optional<KeyValue> {
        val keyValue = keyValueStorage.get(key = key, version = version)
        keyValue.ifPresent { keyValuePresent: KeyValue ->
            keyValuePresent.value = keyValuePresent.value?.let { gnuPG.decrypt(it) }
        }
        return keyValue
    }

    fun save(keyValue: KeyValue): KeyValue {
        keyValue.value = keyValue.value!!.let { gnuPG.encrypt(it) }
        return keyValueStorage.save(keyValue)
    }

    fun update(keyValue: KeyValue): KeyValue {
        keyValue.value = keyValue.value!!.let { gnuPG.encrypt(it) }
        return keyValueStorage.update(keyValue)
    }
}
