package com.arpanrec.bastet.physical.impl

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.exceptions.KeyValueNotFoundException
import com.arpanrec.bastet.physical.KVDataEncrypted
import java.util.Optional


interface KVDataServiceImplInterface {
    fun get(key: String): KVDataEncrypted {
        return get(key, 0)
    }

    fun get(key: String, version: Int): KVDataEncrypted

    fun getMaybe(key: String): Optional<KVDataEncrypted> {
        return getMaybe(key, 0)
    }

    @Throws(CaughtException::class)
    fun getMaybe(key: String, version: Int): Optional<KVDataEncrypted> {
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

    fun save(kvDataEncrypted: KVDataEncrypted) {
        save(kvDataEncrypted, 0)
    }

    fun save(kvDataEncrypted: KVDataEncrypted, version: Int)

    fun update(kvDataEncrypted: KVDataEncrypted, version: Int)

    fun saveOrUpdate(kvDataEncrypted: KVDataEncrypted) {
        saveOrUpdate(kvDataEncrypted, 0)
    }

    fun saveOrUpdate(kvDataEncrypted: KVDataEncrypted, version: Int) {
        if (has(kvDataEncrypted.key, version)) {
            update(kvDataEncrypted, version)
        } else {
            save(kvDataEncrypted, version)
        }
    }

    fun delete(kvDataEncrypted: KVDataEncrypted) {
        delete(kvDataEncrypted, 0)
    }

    fun delete(kvDataEncrypted: KVDataEncrypted, version: Int)

    fun getMaxVersion(key: String): Int

    fun getNewVersion(key: String): Int
}