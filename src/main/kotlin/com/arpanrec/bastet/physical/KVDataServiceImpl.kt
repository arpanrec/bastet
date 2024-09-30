package com.arpanrec.bastet.physical

import com.arpanrec.bastet.physical.impl.Postgres
import org.springframework.stereotype.Component

@Component
class KVDataServiceImpl : KVDataService {

    private val kvDataService: KVDataService = Postgres()

    override fun get(key: String): KVData {
        return this.kvDataService.get(key)
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

    override fun delete(kvData: KVData) {
        this.kvDataService.delete(kvData)
    }
}