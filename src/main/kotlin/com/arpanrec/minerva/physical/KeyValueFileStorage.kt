package com.arpanrec.minerva.physical

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional

@ConfigurationProperties(prefix = "minerva.key-value.file")
class KeyValueFileStorage(path: String) : KeyValueStorage {

    private val storageRootPath: Path = Paths.get(path).toAbsolutePath()

    private val valueFileName = "value.json"

    override fun save(keyValue: KeyValue): KeyValue {
        val keyPath: Path = Paths.get(storageRootPath.toString(), keyValue.key)
        Files.createDirectories(keyPath)
        if (keyValue.version == 0) {
            keyValue.version = getNextVersion(keyPath)
        }
        val keyVersionPath = Paths.get(keyPath.toString(), keyValue.version.toString())
        Files.createDirectories(keyVersionPath)
        val keyVersionFilePath = Paths.get(keyVersionPath.toString(), valueFileName)
        val json = Json.encodeToString(keyValue)
        Files.writeString(keyVersionFilePath, json)
        return keyValue
    }

    override fun update(keyValue: KeyValue): KeyValue {
        TODO("Not yet implemented")
    }

    override fun get(key: String, version: Int): Optional<KeyValue> {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(Paths.get(storageRootPath.toString(), key))
            if (workingVersion == 0) {
                return Optional.empty()
            }
        }

        val filePath = Paths.get(storageRootPath.toString(), key, workingVersion.toString(), valueFileName)
        val json = Files.readString(filePath)
        return Optional.of(Json.decodeFromString(KeyValue.serializer(), json))
    }

    override fun delete(keyValue: KeyValue): KeyValue {
        TODO("Not yet implemented")
    }

    override fun getNextVersion(keyPath: Path): Int {
        return getLatestVersion(keyPath) + 1
    }

    override fun getLatestVersion(keyPath: Path): Int {
        if (!Files.exists(keyPath)) {
            return 0
        }
        val dirs: List<Int> = Files.walk(keyPath, 1).filter { Files.isDirectory(it) && !it.equals(keyPath) }.map {
            it.toString().replaceFirst("${keyPath.toAbsolutePath()}/", "").toInt()
        }.toList().sorted()
        return if (dirs.isEmpty()) 0 else dirs.last()
    }

}