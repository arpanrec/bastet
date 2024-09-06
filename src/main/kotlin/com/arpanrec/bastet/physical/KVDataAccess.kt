package com.arpanrec.bastet.physical

class KVDataAccess {
    fun get(key: String): KVData {
        return KVData(key, "value", mapOf())
    }

    fun saveOrUpdate(kvData: KVData): Boolean {
        return true
    }

    fun delete(kvData: KVData) {
        return
    }
}