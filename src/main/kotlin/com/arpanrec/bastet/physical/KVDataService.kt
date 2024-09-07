package com.arpanrec.bastet.physical

import java.util.Optional

interface KVDataService {
    fun get(key: String): Optional<KVData>
    fun has(key: String): Boolean
    fun saveOrUpdate(kvData: KVData): Boolean
    fun delete(kvData: KVData)
}