package com.arpanrec.bastet.physical

interface KVDataService {
    fun get(key: String): KVData
    fun has(key: String): Boolean
    fun save(kvData: KVData)
    fun update(kvData: KVData)
    fun delete(kvData: KVData)
}