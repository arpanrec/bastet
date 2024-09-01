package com.arpanrec.minerva.physical

import com.arpanrec.minerva.encryption.GnuPG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class KeyValuePersistence(
    @Autowired private val gnuPG: GnuPG
) {
    var internalStorageKey: String = "internal"

    override fun get(key: String): Optional<KVData> {
        return get(key, 0)
    }

    override fun get(key: String, version: Int): Optional<KVData> {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getLatestVersion(key)
            if (workingVersion == 0) {
                return Optional.empty()
            }
        }
        val keyValue = keyValueStorage.get(key, workingVersion)
        if (keyValue.isPresent) {
            val kvData = keyValue.get()
            val decryptedValue = gnuPG.decrypt(kvData.value)
            return Optional.of(KVData(decryptedValue, kvData.metadata))
        }
        return keyValue
    }

    override fun save(key: String, kvData: KVData): Boolean {
        kvData.key = key
        val encryptedKVData = KVData(gnuPG.encrypt(kvData.value), kvData.metadata)
        return keyValueStorage.save(key, encryptedKVData)
    }

    override fun update(key: String, kvData: KVData): Boolean {
        kvData.key = key
        return update(key, kvData, 0)
    }

    override fun update(key: String, kvData: KVData, version: Int): Boolean {
        kvData.key = key
        val encryptedKVData = KVData(gnuPG.encrypt(kvData.value), kvData.metadata)
        return keyValueStorage.update(key, encryptedKVData, version)
    }

    override fun delete(key: String): Boolean {
        return delete(key, 0)
    }

    override fun delete(key: String, version: Int): Boolean {
        return keyValueStorage.delete(key, version)
    }


    override fun getNextVersion(key: String): Int {
        return keyValueStorage.getNextVersion(key)
    }

    override fun getLatestVersion(key: String): Int {
        return keyValueStorage.getLatestVersion(key)
    }

    override fun listVersions(key: String): List<Int> {
        return keyValueStorage.listVersions(key)
    }

    override fun listKeys(key: String): List<String> {
        return keyValueStorage.listKeys(key)
    }
}
