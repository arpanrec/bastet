package com.arpanrec.bastet.physical.jpa

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Serializable
// @Table(name = "kv_data", uniqueConstraints = [UniqueConstraint(columnNames = ["id", "version"])])
@Table(name = "kv_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
data class KVDataDTO(
    @Id @Column(nullable = false, name = "id") var key: String,
    @Column(nullable = true, length = 1024 * 16) var value: String,
    @Column @Convert(converter = JpaMetaDataConverter::class) var metadata: Map<String, String> = HashMap(),
)
