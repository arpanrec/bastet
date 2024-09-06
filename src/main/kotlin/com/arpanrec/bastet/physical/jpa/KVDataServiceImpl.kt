package com.arpanrec.minerva.physical.jpa

import com.arpanrec.minerva.exceptions.MinervaException
import com.arpanrec.minerva.physical.KVData
import com.arpanrec.minerva.physical.KVDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class KVDataServiceImpl(
    @Autowired private val kvDataRepository: KVDataRepository
) : KVDataService {
    override fun get(key: String): Optional<KVData> {
        val allKeys = kvDataRepository.findAllByKey(key)
        if (allKeys.isEmpty()) {
            return Optional.empty()
        }
        if (allKeys.size > 1) {
            throw MinervaException("Multiple keys found")
        }

        return Optional.of(allKeys[0])
    }

    override fun saveOrUpdate(kvData: KVData): Boolean {
        kvDataRepository.save(kvData)
        return true
    }

    override fun delete(kvData: KVData) {
        return kvDataRepository.delete(kvData)
    }
}
