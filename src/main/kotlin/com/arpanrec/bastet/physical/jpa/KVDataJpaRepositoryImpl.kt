package com.arpanrec.bastet.physical.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KVDataJpaRepositoryImpl : JpaRepository<KVDataDTO, String> {
    fun findAllByKey(keyName: String): List<KVDataDTO>
}
