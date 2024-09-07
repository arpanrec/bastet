package com.arpanrec.bastet.physical.jpa

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
internal class KVDataServiceJpaImpl(
    @Autowired private val kvDataRepository: KVDataJpaRepositoryImpl
) : KVDataService {
    override fun get(key: String): Optional<KVData> {
        val allKeys = kvDataRepository.findAllByKey(key)
        if (allKeys.isEmpty()) {
            return Optional.empty()
        }
        if (allKeys.size > 1) {
            throw CaughtException("Multiple keys found")
        }

        return Optional.of(convertToKVData(allKeys[0]))
    }

    override fun has(key: String): Boolean {
        val kv = this.get(key)
        return kv.isPresent
    }

    override fun saveOrUpdate(kvData: KVData): Boolean {
        kvDataRepository.save(convertToKVDataDTO(kvData))
        return true
    }

    override fun delete(kvData: KVData) {
        return kvDataRepository.delete(convertToKVDataDTO(kvData))
    }

    private fun convertToKVData(kvDataDTO: KVDataDTO): KVData {
        return KVData(kvDataDTO.key, kvDataDTO.value, kvDataDTO.metadata)
    }

    private fun convertToKVDataDTO(kvData: KVData): KVDataDTO {
        return KVDataDTO(kvData.key, kvData.value, kvData.metadata)
    }
}
