package com.arpanrec.minerva.test.physical

import com.arpanrec.minerva.physical.KVData
import com.arpanrec.minerva.physical.KeyValuePersistence
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KeyValueTests(@Autowired private val keyValuePersistence: KeyValuePersistence) {

    private val log: Logger = LoggerFactory.getLogger(KeyValueTests::class.java)

    @Test
    fun testSave() {
        val keyValueSave = KVData("1", mapOf("created" to System.currentTimeMillis().toString()))
        keyValuePersistence.save("testSave", KVData("1", mapOf("created" to System.currentTimeMillis().toString())))
        val keyValueGet = keyValuePersistence.get("testSave")
        if (keyValueGet.isPresent) {
            log.info("keyValueGet: {}", keyValueGet.get())
            assert(keyValueGet.get().value == keyValueSave.value) { "keyValueSave.value is not equal to keyValueGet.value" }
        } else {
            assert(false) { "keyValue2 is null" }
        }
    }

    @Test
    fun testSaveVersion() {
        val keyValueSave = KVData("1", mapOf("created" to System.currentTimeMillis().toString()))
        keyValuePersistence.save("testSaveVersion", keyValueSave)
        val keyValueSaveNextVersion = KVData("2", mapOf("created" to System.currentTimeMillis().toString()))
        keyValuePersistence.update("testSaveVersion", keyValueSaveNextVersion)
        val keyValueGet = keyValuePersistence.get("testSaveVersion")
        if (keyValueGet.isPresent) {
            log.info("keyValueGet: {}", keyValueGet.get())
            assert(keyValueGet.get().value == keyValueSaveNextVersion.value) {
                "keyValueGet.value is not equal to keyValueSaveNextVersion.value"
            }
        } else {
            assert(false) { "keyValue2 is null" }
        }

        val keyValueGetOldVersion = keyValuePersistence.get("testSaveVersion", 1)

        if (keyValueGetOldVersion.isPresent) {
            log.info("keyValueGetOldVersion: {}", keyValueGetOldVersion.get())
            assert(keyValueGetOldVersion.get().value == keyValueSave.value) {
                "keyValueGetOldVersion.key is not equal to keyValueSave.key"
            }
        } else {
            assert(false) { "keyValueGetOldVersion is null" }
        }
    }
}
