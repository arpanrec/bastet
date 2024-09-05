package com.arpanrec.minerva.physical

import com.arpanrec.minerva.exceptions.MinervaException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class KVDataService(
    @Autowired private val kvDataRepository: KVDataRepository
) {
    fun get(key: String): Optional<KVData> {
        val allKeys = kvDataRepository.findAllByKey(key)
        if (allKeys.isEmpty()) {
            return Optional.empty()
        }
        if (allKeys.size > 1) {
            throw MinervaException("Multiple keys found")
        }

        return Optional.of(allKeys[0])
    }

    fun saveOrUpdate(kvData: KVData): Boolean {
        kvDataRepository.save(kvData)
        return true
    }

    fun delete(kvData: KVData) {
        return kvDataRepository.delete(kvData)
    }
}
