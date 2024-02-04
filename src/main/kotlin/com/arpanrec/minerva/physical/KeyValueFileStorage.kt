package com.arpanrec.minerva.physical

import com.arpanrec.minerva.exceptions.MinervaException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

@Lazy
@Component
class KeyValueFileStorage(@Value("\${minerva.physical.key-value-file-storage.path:./storage}") private val path: String) :
    KeyValueStorage {

    private val storageRootPath: String = Paths.get(path).toAbsolutePath().toString()

    private val valueFileName = "value.json"

    override fun get(key: String): Optional<KeyValue> {
        return get(key, 0)
    }

    override fun get(key: String, version: Int): Optional<KeyValue> {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(Paths.get(storageRootPath, key))
            if (workingVersion == 0) {
                return Optional.empty()
            }
        }

        val filePath: Path = Paths.get(storageRootPath, key, workingVersion.toString(), valueFileName)
        val json: String = Files.readString(filePath)
        return Optional.of(Json.decodeFromString(KeyValue.serializer(), json))
    }

    override fun save(keyValue: KeyValue): KeyValue {
        if (getLatestVersion(Paths.get(storageRootPath, keyValue.key!!)) > 0) {
            throw MinervaException("Key ${keyValue.key} already exists")
        }
        return saveOrUpdate(keyValue)
    }

    override fun update(keyValue: KeyValue): KeyValue {
        if (getLatestVersion(Paths.get(storageRootPath, keyValue.key!!)) == 0) {
            throw MinervaException("Key ${keyValue.key} does not exist")
        }
        return saveOrUpdate(keyValue)
    }

    private fun saveOrUpdate(keyValue: KeyValue): KeyValue {
        val keyPath: Path = Paths.get(storageRootPath, keyValue.key)
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

    override fun delete(keyValue: KeyValue): Boolean {
        return delete(keyValue, 0)
    }

    @OptIn(ExperimentalPathApi::class)
    override fun delete(keyValue: KeyValue, version: Int): Boolean {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(Paths.get(storageRootPath, keyValue.key!!))
            if (workingVersion == 0) {
                throw MinervaException("Key ${keyValue.key} with version ${keyValue.version} does not exist")
            }
        }
        val keyPath: Path = Paths.get(storageRootPath, keyValue.key!!)
        val keyVersionPath = Paths.get(keyPath.toString(), workingVersion.toString())
        keyVersionPath.deleteRecursively()
        return true
    }

    override fun deleteAllVersions(keyValue: KeyValue): Boolean {
        val allVersions = listVersions(Paths.get(storageRootPath, keyValue.key))
        if (allVersions.isEmpty()) {
            throw MinervaException("Key ${keyValue.key} does not exist")
        }
        allVersions.forEach { version: Int ->
            val keyValueToDelete = get(keyValue.key!!, version)
            if (keyValueToDelete.isPresent) {
                delete(keyValueToDelete.get())
            }
        }
        return true
    }

    override fun getNextVersion(keyPath: Path): Int {
        return getLatestVersion(keyPath) + 1
    }

    override fun getLatestVersion(keyPath: Path): Int {
        val dirs: List<Int> = listVersions(keyPath)
        return if (dirs.isEmpty()) 0 else dirs.last()
    }

    override fun listVersions(keyPath: Path): List<Int> {
        return if (!Files.exists(keyPath)) {
            emptyList()
        } else {
            Files.walk(keyPath, 1).filter { Files.isDirectory(it) && !it.equals(keyPath) }.map {
                it.toString().replaceFirst("${keyPath.toAbsolutePath()}/", "").toInt()
            }.toList().sorted()
        }
    }

    override fun listKeys(keyPath: Path): List<String> {
        return Files.walk(keyPath, 1).filter { Files.isDirectory(it) && !it.equals(keyPath) }.map {
            it.toString().replaceFirst("${keyPath.toAbsolutePath()}/", "")
        }.toList()
    }
}
