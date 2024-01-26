package com.arpanrec.minerva.physical

import java.nio.file.Path
import java.util.*


interface KeyValueStorage {
    fun save(keyValue: KeyValue): KeyValue
    fun update(keyValue: KeyValue): KeyValue
    fun get(key: String, version: Int): Optional<KeyValue>
    fun delete(keyValue: KeyValue): KeyValue
    fun getNextVersion(keyPath: Path): Int
    fun getLatestVersion(keyPath: Path): Int
}