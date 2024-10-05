package com.arpanrec.bastet.physical

import com.arpanrec.bastet.physical.impl.Postgres
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class KVDataServiceImpl(@Autowired private val kvDataService: Postgres) : KVDataService {
    override fun get(key: String, version: Int): KVData {
        return kvDataService.get(key, version)
    }

    override fun getMaybe(key: String, version: Int): Optional<KVData> {
        return kvDataService.getMaybe(key, version)
    }

    override fun has(key: String, version: Int): Boolean {
        return kvDataService.has(key, version)
    }

    override fun save(kvData: KVData, version: Int) {
        kvDataService.save(kvData, version)
    }

    override fun update(kvData: KVData, version: Int) {
        kvDataService.update(kvData, version)
    }

    override fun delete(kvData: KVData, version: Int) {
        kvDataService.delete(kvData, version)
    }

    override fun getMaxVersion(key: String): Int {
        return kvDataService.getMaxVersion(key)
    }

    override fun getNewVersion(key: String): Int {
        return kvDataService.getNewVersion(key)
    }

}