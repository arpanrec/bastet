package com.arpanrec.minerva.physical

import com.arpanrec.minerva.gnupg.GnuPG
import jakarta.annotation.PostConstruct
import java.util.Optional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class KeyValuePersistence(@Value("\${minerva.physical.key-value-persistence.persistence-type:FILE}") private val persistenceType: KeyValuePersistenceType) {

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
