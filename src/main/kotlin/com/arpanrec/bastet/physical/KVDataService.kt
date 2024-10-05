package com.arpanrec.bastet.physical

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.exceptions.KeyValueNotFoundException
import java.util.Optional


interface KVDataService {
    fun get(key: String): KVData {
        return get(key, 0)
    }

    fun get(key: String, version: Int): KVData

    fun getMaybe(key: String): Optional<KVData> {
        return getMaybe(key, 0)
    }

    @Throws(CaughtException::class)
    fun getMaybe(key: String, version: Int): Optional<KVData> {
        return try {
            Optional.of(get(key, version))
        } catch (e: KeyValueNotFoundException) {
            Optional.empty()
        } catch (e: Exception) {
            throw CaughtException("Caught exception", e)
        }
    }

    fun has(key: String): Boolean {
        return has(key, 0)
    }

    fun has(key: String, version: Int): Boolean

    fun save(kvData: KVData) {
        save(kvData, 0)
    }

    fun save(kvData: KVData, version: Int)

    fun update(kvData: KVData, version: Int)

    fun saveOrUpdate(kvData: KVData) {
        saveOrUpdate(kvData, 0)
    }

    fun saveOrUpdate(kvData: KVData, version: Int) {
        if (has(kvData.key, version)) {
            update(kvData, version)
        } else {
            save(kvData, version)
        }
    }

    fun delete(kvData: KVData) {
        delete(kvData, 0)
    }

    fun delete(kvData: KVData, version: Int)

    fun getMaxVersion(key: String): Int

    fun getNewVersion(key: String): Int
}