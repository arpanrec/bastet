package com.arpanrec.minerva.test.physical

import com.arpanrec.minerva.physical.KeyValue
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
        val keyValueSave = KeyValue("testSave", "1")
        keyValuePersistence.save(keyValueSave)
        val keyValueGet = keyValuePersistence.get("testSave")
        if (keyValueGet.isPresent) {
            log.info("keyValueGet: {}", keyValueGet.get())
            assert(keyValueGet.get().key == keyValueSave.key) { "keyValueSave.key is not equal to keyValueGet.key" }
        } else {
            assert(false) { "keyValue2 is null" }
        }
    }

    @Test
    fun testSaveVersion() {
        val keyValueSave = KeyValue("testSaveVersion", "1")
        keyValuePersistence.save(keyValueSave)
        val keyValueSaveNextVersion = KeyValue("testSaveVersion", "2")
        keyValuePersistence.update(keyValueSaveNextVersion)
        val keyValueGet = keyValuePersistence.get("testSaveVersion")
        if (keyValueGet.isPresent) {
            log.info("keyValueGet: {}", keyValueGet.get())
            assert(keyValueGet.get().key == keyValueSaveNextVersion.key) {
                "keyValueGet.key is not equal to keyValueSaveNextVersion.key"
            }
        } else {
            assert(false) { "keyValue2 is null" }
        }

        val keyValueGetOldVersion = keyValuePersistence.get("testSaveVersion", 1)

        if (keyValueGetOldVersion.isPresent) {
            log.info("keyValueGetOldVersion: {}", keyValueGetOldVersion.get())
            assert(keyValueGetOldVersion.get().key == keyValueSave.key) {
                "keyValueGetOldVersion.key is not equal to keyValueSave.key"
            }
        } else {
            assert(false) { "keyValueGetOldVersion is null" }
        }
    }
}
