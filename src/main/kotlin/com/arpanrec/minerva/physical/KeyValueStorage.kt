package com.arpanrec.minerva.physical

import java.util.Optional

interface KeyValueStorage {

    fun save(keyValue: KeyValue): Boolean

    fun update(keyValue: KeyValue): Boolean

    fun get(key: String): Optional<KeyValue> {
        return get(key, 0)
    }

    fun get(key: String, version: Int): Optional<KeyValue>

    fun delete(keyValue: KeyValue): Boolean

    fun getNextVersion(key: String): Int

    fun getLatestVersion(key: String): Int

    fun listVersions(key: String): List<Int>

    fun listKeys(key: String): List<String>
}
