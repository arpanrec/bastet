package com.arpanrec.bastet.physical.impl

import com.arpanrec.bastet.exceptions.KeyValueNotFoundException
import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
import org.springframework.boot.configurationprocessor.json.JSONObject
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

    @Throws(KeyValueNotFoundException::class)
    override fun get(key: String, version: Int): KVData {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        connection.prepareStatement("SELECT * FROM kv WHERE key = ? AND version = ? AND to_be_deleted = false")
            .use { stmt ->
                stmt.setString(1, key)
                stmt.setInt(2, workingVersion)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return KVData(rs.getString("key"), rs.getString("value"))
                    }
                }
            }
        throw KeyValueNotFoundException("Key not found")
    }


    override fun has(key: String, version: Int): Boolean {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(key)
        }
        if (workingVersion == 0) {
            return false
        }
        return true
    }

    override fun save(kvData: KVData, version: Int) {
        var workingVersion = getNewVersion(kvData.key)
        if (workingVersion == 0) {
            workingVersion = 1
        }
        val metaDataString = JSONObject(kvData.metadata).toString()
        connection.prepareStatement(
            "INSERT INTO kv (key, value, version, metadata, to_be_deleted) VALUES (?, ?, ?, ?, ?)"
        )
            .use { stmt ->
                stmt.setString(1, kvData.key)
                stmt.setString(2, kvData.value)
                stmt.setInt(3, workingVersion)
                stmt.setString(4, metaDataString)
                stmt.setBoolean(5, false)
                stmt.executeUpdate()
            }
        connection.commit()

    }

    override fun update(kvData: KVData, version: Int) {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(kvData.key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        val metaDataString = JSONObject(kvData.metadata).toString()
        connection.prepareStatement(
            "UPDATE kv SET value = ?, metadata = ? WHERE key = ? AND version = ? AND to_be_deleted = false"
        )
            .use { stmt ->
                stmt.setString(1, kvData.value)
                stmt.setString(2, metaDataString)
                stmt.setString(3, kvData.key)
                stmt.setInt(4, workingVersion)
                stmt.executeUpdate()
            }
        connection.commit()
    }

    override fun delete(kvData: KVData, version: Int) {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(kvData.key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        connection.prepareStatement("UPDATE kv SET to_be_deleted = true WHERE key = ? AND version = ?")
            .use { stmt ->
                stmt.setString(1, kvData.key)
                stmt.setInt(2, workingVersion)
                stmt.executeUpdate()
            }
    }


    override fun getMaxVersion(key: String): Int {
        connection.prepareStatement("SELECT MAX(version) FROM kv WHERE key = ? AND to_be_deleted = false")
            .use { stmt ->
                stmt.setString(1, key)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        return 0
    }

    override fun getNewVersion(key: String): Int {
        connection.prepareStatement("SELECT MAX(version) FROM kv WHERE key = ?")
            .use { stmt ->
                stmt.setString(1, key)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return rs.getInt(1) + 1
                    }
                }
            }
        return 1
    }
}