package com.arpanrec.bastet.physical.jpa

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService
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
            throw CaughtException("Multiple keys found")
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
