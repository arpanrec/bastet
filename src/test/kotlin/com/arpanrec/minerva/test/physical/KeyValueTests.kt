package com.arpanrec.minerva.test.physical

import com.arpanrec.minerva.physical.KVData
import com.arpanrec.minerva.physical.KVDataService
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KeyValueTests(@Autowired private val kVDataService: KVDataService) {

    private val log: Logger = LoggerFactory.getLogger(KeyValueTests::class.java)

    private fun getRandomKey(): String {
        return "test" + System.currentTimeMillis()
    }

    @Test
    fun testSave() {
        val key = getRandomKey()
        val keyValueSave = KVData(key, "1", mapOf("created" to System.currentTimeMillis().toString()))
        kVDataService.saveOrUpdate(keyValueSave)
        val keyValueGet = kVDataService.get(key)
        if (keyValueGet.isPresent) {
            log.info("keyValueGet: {}", keyValueGet.get())
            assert(keyValueGet.get().value == keyValueSave.value) { "keyValueSave.value is not equal to keyValueGet.value" }
        } else {
            assert(false) { "keyValue2 is null" }
        }
    }

//    @Test
//    fun testSaveVersion() {
//        val keyValueSave = KVData("1", mapOf("created" to System.currentTimeMillis().toString()))
//        keyValuePersistence.save("testSaveVersion", keyValueSave)
//        val keyValueSaveNextVersion = KVData("2", mapOf("created" to System.currentTimeMillis().toString()))
//        keyValuePersistence.update("testSaveVersion", keyValueSaveNextVersion)
//        val keyValueGet = keyValuePersistence.get("testSaveVersion")
//        if (keyValueGet.isPresent) {
//            log.info("keyValueGet: {}", keyValueGet.get())
//            assert(keyValueGet.get().value == keyValueSaveNextVersion.value) {
//                "keyValueGet.value is not equal to keyValueSaveNextVersion.value"
//            }
//        } else {
//            assert(false) { "keyValue2 is null" }
//        }
//
//        val keyValueGetOldVersion = keyValuePersistence.get("testSaveVersion", 1)
//
//        if (keyValueGetOldVersion.isPresent) {
//            log.info("keyValueGetOldVersion: {}", keyValueGetOldVersion.get())
//            assert(keyValueGetOldVersion.get().value == keyValueSave.value) {
//                "keyValueGetOldVersion.key is not equal to keyValueSave.key"
//            }
//        } else {
//            assert(false) { "keyValueGetOldVersion is null" }
//        }
//    }
}
