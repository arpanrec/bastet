package com.arpanrec.bastet.physical

import java.util.*

interface KVDataService {
    fun get(key: String): KVData
    fun getMaybe(key: String): Optional<KVData>
    fun has(key: String): Boolean
    fun save(kvData: KVData)
    fun update(kvData: KVData)
    fun saveOrUpdate(kvData: KVData)
    fun delete(kvData: KVData)
}