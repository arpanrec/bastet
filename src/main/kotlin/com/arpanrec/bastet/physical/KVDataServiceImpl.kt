package com.arpanrec.bastet.physical

import com.arpanrec.bastet.encryption.AES256CBC
import com.arpanrec.bastet.physical.jpa.KVDataServiceJpaImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class KVDataServiceImpl(
) : KVDataService {
    private lateinit var kvDataService: KVDataService
    private lateinit var aeS256CBC: AES256CBC

    private constructor(@Autowired kvDataServiceJpaImpl: KVDataServiceJpaImpl, @Autowired aes256CBC: AES256CBC) : this() {
        this.kvDataService = kvDataServiceJpaImpl; this.aeS256CBC = aes256CBC
    }

    override fun get(key: String): Optional<KVData> {
        val kvData = kvDataService.get(key)
        if (kvData.isPresent) {
            val decryptedValue = aeS256CBC.decrypt(kvData.get().value)
            kvData.get().value = decryptedValue
            return Optional.of(kvData.get())
        }
        return Optional.empty()
    }

    override fun saveOrUpdate(kvData: KVData): Boolean {
        val encryptedValue = aeS256CBC.encrypt(kvData.value)
        kvData.value = encryptedValue
        return kvDataService.saveOrUpdate(kvData)
    }

    override fun delete(kvData: KVData) {
        return kvDataService.delete(kvData)
    }
}