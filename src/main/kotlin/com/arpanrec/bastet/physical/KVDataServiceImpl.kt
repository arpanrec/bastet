package com.arpanrec.bastet.physical

import com.arpanrec.bastet.encryption.GnuPG
import com.arpanrec.bastet.physical.jpa.KVDataServiceJpaImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class KVDataServiceImpl() : KVDataService {
    private lateinit var kvDataService: KVDataService
    private lateinit var gnuPG: GnuPG

    private constructor(@Autowired kvDataServiceJpaImpl: KVDataServiceJpaImpl, @Autowired gnuPG: GnuPG) : this() {
        this.kvDataService = kvDataServiceJpaImpl; this.gnuPG = gnuPG
    }

    override fun get(key: String): Optional<KVData> {
        val kvData = kvDataService.get(key)
        if (kvData.isPresent) {
            val decryptedValue = gnuPG.decrypt(kvData.get().value)
            kvData.get().value = decryptedValue
            return Optional.of(kvData.get())
        }
        return Optional.empty()
    }

    override fun has(key: String): Boolean {
        return kvDataService.has(key)
    }

    override fun saveOrUpdate(kvData: KVData): Boolean {
        val encryptedValue = gnuPG.encrypt(kvData.value)
        kvData.value = encryptedValue
        return kvDataService.saveOrUpdate(kvData)
    }

    override fun delete(kvData: KVData) {
        return kvDataService.delete(kvData)
    }
}