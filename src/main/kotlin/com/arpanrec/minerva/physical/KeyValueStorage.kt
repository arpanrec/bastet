package com.arpanrec.minerva.physical

import java.nio.file.Path
import java.util.Optional

interface KeyValueStorage {
    fun save(keyValue: KeyValue): KeyValue
    fun update(keyValue: KeyValue): KeyValue
    fun get(key: String, version: Int = 0): Optional<KeyValue>
    fun delete(keyValue: KeyValue, version: Int = 0): KeyValue
    fun getNextVersion(keyPath: Path): Int
    fun getLatestVersion(keyPath: Path): Int
    fun listKeys(keyPath: Path): List<String>
}
