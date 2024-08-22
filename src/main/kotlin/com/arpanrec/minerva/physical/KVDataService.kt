package com.arpanrec.minerva.physical

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class KVDataService(
    @Autowired private val kvDataRepository: KVDataRepository
) : KeyValueStorage {
    override fun get(key: String, version: Int): Optional<KVData> {
        return Optional.ofNullable(kvDataRepository.findDistinctTopByKeyAndVersion(key, version))
    }

    override fun save(key: String, kvData: KVData): Boolean {
        kvData.key = key
        kvData.version = 1
        kvDataRepository.save(kvData)
        return true
    }

    override fun update(key: String, kvData: KVData, version: Int): Boolean {
        val current = kvDataRepository.findDistinctTopByKeyAndVersion(key, version)
        if (current != null) {
            current.value = kvData.value
            current.metadata = kvData.metadata
            kvDataRepository.save(current)
            return true
        }
        throw IllegalArgumentException("No data found for key $key and version $version")
    }

    override fun delete(key: String, version: Int): Boolean {
        return kvDataRepository.deleteByKeyAndVersion(key, version) > 0
    }

    override fun getNextVersion(key: String): Int {
        val allVersions = listVersions(key)
        return if (allVersions.isEmpty()) {
            1
        } else {
            allVersions.maxOrNull()!! + 1
        }
    }

    override fun getLatestVersion(key: String): Int {
        val allVersions = listVersions(key)
        return if (allVersions.isEmpty()) {
            0
        } else {
            allVersions.maxOrNull()!!
        }
    }

    override fun listVersions(key: String): List<Int> {
        kvDataRepository.findAllByKey(key).let { kvDataList ->
            return kvDataList.map { kvData -> kvData.version!! }
        }
    }

    override fun listKeys(key: String): List<String> {
        TODO("Not yet implemented")
    }
}