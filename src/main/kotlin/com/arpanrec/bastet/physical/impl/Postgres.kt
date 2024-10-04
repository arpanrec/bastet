package com.arpanrec.bastet.physical.impl

import com.arpanrec.bastet.exceptions.CaughtException
import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
import java.util.Optional
import kotlin.collections.HashMap

@Component
class Postgres : KVDataService {

    private val log = LoggerFactory.getLogger(Postgres::class.java)

    @PreDestroy
    fun closeConnection() {
        if (!connection.isClosed) {
            log.info("Closing connection")
            connection.close()
        }
    }

    private val url: String = "jdbc:postgresql://127.0.0.1:5432/bastet?currentSchema=bastet"
    private val properties: HashMap<String, String> =
        hashMapOf("user" to "postgres", "password" to "postgres", "ssl" to "false")
    private val connection: Connection = DriverManager.getConnection(url, Properties().apply { putAll(properties) })

    override fun get(key: String): KVData {
        connection.prepareStatement("SELECT * FROM kv WHERE key = ?").use { stmt ->
            stmt.setString(1, key)
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    return KVData(rs.getString("key"), rs.getString("value"))
                }
            }
        }
        throw CaughtException("Key not found")
    }

    override fun getMaybe(key: String): Optional<KVData> {
        TODO("Not yet implemented")
    }

    override fun has(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun save(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun update(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun saveOrUpdate(kvData: KVData) {
        TODO("Not yet implemented")
    }

    override fun delete(kvData: KVData) {
        TODO("Not yet implemented")
    }
}