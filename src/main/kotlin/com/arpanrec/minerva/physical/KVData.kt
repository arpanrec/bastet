package com.arpanrec.minerva.physical

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Serializable
@Table(name = "kv_data", uniqueConstraints = [UniqueConstraint(columnNames = ["key", "version"])])
@Data
@AllArgsConstructor
@NoArgsConstructor
open class KVData(
    @Column(nullable = true, length = 1024 * 16) var value: String,
    @Column @Convert(converter = MetaDataConverter::class) var metadata: Map<String, String>
) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings("kotlin:S117")
    var id: Long? = null

    @Column(nullable = false, name = "key")
    var key: String? = null

    @Column(nullable = false, name = "version")
    var version: Int? = null
}
