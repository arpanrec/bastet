package com.arpanrec.minerva.physical

import java.util.Optional

interface KeyValueStorage {

    fun get(key: String): Optional<KVData> {
        return get(key, 0)
    }

    fun get(key: String, version: Int): Optional<KVData>

    fun save(key: String, kvData: KVData): Boolean

    fun update(key: String, kvData: KVData): Boolean {
        return update(key, kvData, 0)
    }

    fun update(key: String, kvData: KVData, version: Int): Boolean


    fun delete(key: String): Boolean {
        return delete(key, 0)
    }

    fun delete(key: String, version: Int): Boolean

    fun getNextVersion(key: String): Int

    fun getLatestVersion(key: String): Int

    fun listVersions(key: String): List<Int>

    fun listKeys(key: String): List<String>
}
