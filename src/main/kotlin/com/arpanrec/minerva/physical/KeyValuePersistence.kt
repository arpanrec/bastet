package com.arpanrec.minerva.physical

import java.util.Optional

class KeyValuePersistence {

    private val keyValueStorage: KeyValueStorage = KeyValueFileStorage()

    fun get(key: String, version: Int = 0): Optional<KeyValue> {
        return keyValueStorage.get(key = key, version = version)
    }

    fun save(keyValue: KeyValue): KeyValue {
        return keyValueStorage.save(keyValue)
    }
}
