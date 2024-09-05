package com.arpanrec.minerva.physical

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KVDataRepository : JpaRepository<KVData, String> {
    fun findAllByKey(keyName: String): List<KVData>
}
