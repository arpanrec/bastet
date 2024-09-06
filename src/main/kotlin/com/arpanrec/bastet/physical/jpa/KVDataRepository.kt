package com.arpanrec.bastet.physical.jpa

import com.arpanrec.bastet.physical.KVData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KVDataRepository : JpaRepository<KVData, String> {
    fun findAllByKey(keyName: String): List<KVData>
}
