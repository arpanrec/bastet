package com.arpanrec.bastet.physical

import com.arpanrec.bastet.encryption.AES256CBC
import com.arpanrec.bastet.physical.impl.Postgres
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class KVDataService(
    @Autowired private val kvDataServiceImpl: Postgres,
    @Autowired private val aes256CBC: AES256CBC
) {

    private val objectMapper = jacksonObjectMapper()

    fun getEncrypted(key: String): KVDataEncrypted {
        return kvDataServiceImpl.get(key)
    }

    fun saveEncrypted(kvDataEncrypted: KVDataEncrypted) {
        kvDataServiceImpl.save(kvDataEncrypted)
    }

    fun get(key: String, version: Int): KVData {
        val encryptedKV = kvDataServiceImpl.get(key, version)
        return KVData(encryptedKV.key, aes256CBC.decrypt(encryptedKV.value), encryptedKV.metadata)
    }

    fun getMaybe(key: String): Optional<KVData> {
        val encryptedKV = kvDataServiceImpl.getMaybe(key, version)
        if (encryptedKV.isPresent) {
            return Optional.of(decrypt(encryptedKV.get()))
        }
        return Optional.empty()
    }

    fun has(key: String, version: Int): Boolean {
        return kvDataServiceImpl.has(key, version)
    }

    fun save(kvData: KVData, version: Int) {
        val kvDataEncrypted = encrypt(kvData)
        kvDataServiceImpl.save(kvDataEncrypted, version)
    }

    fun update(kvData: KVData, version: Int) {
        val kvDataEncrypted = encrypt(kvData)
        kvDataServiceImpl.update(kvDataEncrypted, version)
    }

    fun delete(kvData: KVData, version: Int) {
        val kvDataEncrypted = encrypt(kvData)
        kvDataServiceImpl.delete(kvDataEncrypted, version)
    }

    fun getMaxVersion(key: String): Int {
        return kvDataServiceImpl.getMaxVersion(key)
    }

    fun getNewVersion(key: String): Int {
        return kvDataServiceImpl.getNewVersion(key)
    }
}