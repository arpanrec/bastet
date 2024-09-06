package com.arpanrec.bastet.physical

import java.util.Optional

interface KVDataService {
    fun get(key: String): Optional<KVData>
    fun saveOrUpdate(kvData: KVData): Boolean
    fun delete(kvData: KVData)
}