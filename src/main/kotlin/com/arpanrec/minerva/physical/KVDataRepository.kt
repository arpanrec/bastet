package com.arpanrec.minerva.physical

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KVDataRepository : JpaRepository<KVData?, Long?> {
    fun findAllByKey(keyName: String): List<KVData>

    fun findDistinctTopByKeyAndVersion(keyName: String, version: Int): KVData?

    fun deleteByKeyAndVersion(keyName: String, version: Int): Int
}
