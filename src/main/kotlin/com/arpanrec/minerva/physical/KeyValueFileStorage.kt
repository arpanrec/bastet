package com.arpanrec.minerva.physical

import com.arpanrec.minerva.exceptions.MinervaException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val log: Logger = LoggerFactory.getLogger(KeyValueFileStorage::class.java)

    private val storageRootPath: String = Paths.get(path).toAbsolutePath().toString()

    private val valueFileName = "value.json"

    private fun keyTransformer(key: String): String {
        return key.lowercase()
    }

    override fun get(key: String, version: Int): Optional<KVData> {
        val transformedKey = keyTransformer(key)
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(transformedKey)
            if (workingVersion == 0) {
                return Optional.empty()
            }
        }

        val filePath: Path = Paths.get(storageRootPath, transformedKey, workingVersion.toString(), valueFileName)
        val json: String = Files.readString(filePath)
        return Optional.of(Json.decodeFromString(KVData.serializer(), json))
    }

    override fun save(key: String, kvData: KVData): Boolean {
        if (getLatestVersion(key) > 0) {
            throw MinervaException("Key $key already exists")
        }
        return saveOrUpdate(key, kvData, 0)
    }

    override fun update(key: String, kvData: KVData, version: Int): Boolean {
        if (getLatestVersion(key) == 0) {
            throw MinervaException("Key $key does not exist")
        }
        return saveOrUpdate(key, kvData, version)
    }

    private fun saveOrUpdate(key: String, kvData: KVData, version: Int): Boolean {
        val transformedKey = keyTransformer(key)
        var workingVersion = version
        val keyPath: Path = Paths.get(storageRootPath, transformedKey)
        Files.createDirectories(keyPath)
        if (workingVersion == 0) {
            workingVersion = getNextVersion(transformedKey)
        }
        val keyVersionPath = Paths.get(keyPath.toString(), workingVersion.toString())
        Files.createDirectories(keyVersionPath)
        val keyVersionFilePath = Paths.get(keyVersionPath.toString(), valueFileName)
        val json = Json.encodeToString(kvData)
        Files.writeString(keyVersionFilePath, json)
        return true
    }

    @OptIn(ExperimentalPathApi::class)
    override fun delete(key: String, version: Int): Boolean {
        val transformedKey = keyTransformer(key)
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(transformedKey)
            if (workingVersion == 0) {
                throw MinervaException("Key $key with does not exist")
            }
        }

        val keyPath: Path = Paths.get(storageRootPath, transformedKey)
        val keyVersionPath = Paths.get(keyPath.toString(), workingVersion.toString())
        keyVersionPath.deleteRecursively()
        return true
    }

    override fun getNextVersion(key: String): Int {
        return getLatestVersion(key) + 1
    }

    override fun getLatestVersion(key: String): Int {
        val dirs: List<Int> = listVersions(key)
        val lastVersion = if (dirs.isEmpty()) 0 else dirs.last()
        return lastVersion
    }

    override fun listVersions(key: String): List<Int> {
        val transformedKey = keyTransformer(key)
        val keyPath: Path = Paths.get(storageRootPath, transformedKey)
        val versionsList: List<Int>
        if (!Files.exists(keyPath)) {
            log.debug("No versions found for key: {}, in Path {}", key, keyPath)
            return emptyList()
        } else {
            versionsList = Files.walk(keyPath, 1).filter { Files.isDirectory(it) && !it.equals(keyPath) }.map {
                it.toString().replaceFirst("${keyPath.toAbsolutePath()}/", "").toInt()
            }.toList().sorted()
            log.debug("Versions found for key: {} are: {}, in Path: {}", key, versionsList, keyPath)
            return versionsList
        }
    }

    override fun listKeys(key: String): List<String> {
        val transformedKey = keyTransformer(key)
        val keyPath: Path = Paths.get(storageRootPath, transformedKey)
        return Files.walk(keyPath, 1).filter { Files.isDirectory(it) && !it.equals(keyPath) }.map {
            it.toString().replaceFirst("${keyPath.toAbsolutePath()}/", "")
        }.toList()
    }
}
