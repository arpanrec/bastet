package com.arpanrec.bastet.physical.impl

import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService

class Postgres(
    private val host: String = "localhost",
    private val port: Int = 5432,
    private val user: String = "postgres",
) : KVDataService {

    override fun get(key: String): KVData {
        TODO("Not yet implemented")
    }

    override fun has(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun save(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun update(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun delete(kvData: KVData) {
        TODO("Not yet implemented")
    }
}