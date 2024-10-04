package com.arpanrec.bastet.physical

import com.arpanrec.bastet.physical.impl.Postgres
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*

@Component
class KVDataServiceImpl(@Autowired private val kvDataService: Postgres) : KVDataService {


    override fun get(key: String): KVData {
        return this.kvDataService.get(key)
    }

    override fun getMaybe(key: String): Optional<KVData> {
        TODO("Not yet implemented")
    }

    override fun has(key: String): Boolean {
        return this.kvDataService.has(key)
    }

    override fun save(kvData: KVData) {
        this.kvDataService.save(kvData)
    }

    override fun update(kvData: KVData) {
        this.kvDataService.update(kvData)
    }

    override fun saveOrUpdate(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun delete(kvData: KVData) {
        this.kvDataService.delete(kvData)
    }
}