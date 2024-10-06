package com.arpanrec.bastet.physical.impl

import com.arpanrec.bastet.exceptions.KeyValueNotFoundException
import com.arpanrec.bastet.physical.KVDataEncrypted
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
import kotlin.collections.HashMap

@Component
class Postgres : KVDataServiceImplInterface {

    private val log = LoggerFactory.getLogger(Postgres::class.java)

    @PreDestroy
    fun closeConnection() {
        if (!connection.isClosed) {
            log.info("Closing connection")
            connection.close()
        }
    }

    private fun commit(connection: Connection) {
        if (properties.keys.contains("autocommit") && properties["autocommit"] == "false") {
            connection.commit()
        }
    }

    private val url: String = "jdbc:postgresql://127.0.0.1:5432/bastet?currentSchema=bastet"
    private val properties: HashMap<String, String> =
        hashMapOf("user" to "postgres", "password" to "postgres", "ssl" to "false")
    private val connection: Connection = DriverManager.getConnection(url, Properties().apply { putAll(properties) })

    @Throws(KeyValueNotFoundException::class)
    override fun get(key: String, version: Int): KVDataEncrypted {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        this.connection.prepareStatement("SELECT * FROM kv WHERE key = ? AND version = ?")
            .use { stmt ->
                stmt.setString(1, key)
                stmt.setInt(2, workingVersion)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return KVDataEncrypted(rs.getString("key"), rs.getString("value"))
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

    override fun save(kvDataEncrypted: KVDataEncrypted, version: Int) {
        var workingVersion = getNewVersion(kvDataEncrypted.key)
        if (workingVersion == 0) {
            workingVersion = 1
        }
        this.connection.prepareStatement(
            "INSERT INTO kv (key, value, version, metadata) VALUES (?, ?, ?, ?)"
        )
            .use { stmt ->
                stmt.setString(1, kvDataEncrypted.key)
                stmt.setString(2, kvDataEncrypted.value)
                stmt.setInt(3, workingVersion)
                stmt.setString(4, kvDataEncrypted.metadata)
                stmt.executeUpdate()
            }
        this.commit(this.connection)

    }

    override fun update(kvDataEncrypted: KVDataEncrypted, version: Int) {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(kvDataEncrypted.key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        this.connection.prepareStatement(
            "UPDATE kv SET value = ?, metadata = ? WHERE key = ? AND version = ?"
        )
            .use { stmt ->
                stmt.setString(1, kvDataEncrypted.value)
                stmt.setString(2, kvDataEncrypted.metadata)
                stmt.setString(3, kvDataEncrypted.key)
                stmt.setInt(4, workingVersion)
                stmt.executeUpdate()
            }
        this.commit(this.connection)
    }

    override fun delete(kvDataEncrypted: KVDataEncrypted, version: Int) {
        var workingVersion = version
        if (workingVersion == 0) {
            workingVersion = getMaxVersion(kvDataEncrypted.key)
        }
        if (workingVersion == 0) {
            throw KeyValueNotFoundException("Key not found")
        }
        this.connection.prepareStatement("DELETE FROM kv WHERE key = ? AND version = ?")
            .use { stmt ->
                stmt.setString(1, kvDataEncrypted.key)
                stmt.setInt(2, workingVersion)
                stmt.executeUpdate()
            }
    }


    override fun getMaxVersion(key: String): Int {
        this.connection.prepareStatement("SELECT MAX(version) FROM kv WHERE key = ?")
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
        this.connection.prepareStatement("SELECT MAX(version) FROM kv WHERE key = ?")
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