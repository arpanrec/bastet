package com.arpanrec.minerva.physical

import java.nio.file.Path
import java.util.Optional

interface KeyValueStorage {

    fun save(keyValue: KeyValue): KeyValue

    fun update(keyValue: KeyValue): KeyValue

    fun get(key: String): Optional<KeyValue>

    fun get(key: String, version: Int): Optional<KeyValue>

    fun delete(keyValue: KeyValue, version: Int): Boolean

    fun delete(keyValue: KeyValue): Boolean

    fun deleteAllVersions(keyValue: KeyValue): Boolean

    fun getNextVersion(keyPath: Path): Int

    fun getLatestVersion(keyPath: Path): Int

    fun listVersions(keyPath: Path): List<Int>

    fun listKeys(keyPath: Path): List<String>
}
