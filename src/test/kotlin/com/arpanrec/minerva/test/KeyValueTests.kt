package com.arpanrec.minerva.test

import com.arpanrec.minerva.physical.KeyValue
import com.arpanrec.minerva.physical.KeyValuePersistence
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class KeyValueTests {

    private val log: Logger = LoggerFactory.getLogger(KeyValueTests::class.java)

    @Autowired
    private var keyValuePersistence: KeyValuePersistence? = null

    @Test
    fun testSave() {
        val keyValue = KeyValue("key", "value")
        keyValuePersistence!!.save(keyValue)
        val keyValue2 = keyValuePersistence!!.get("key")
        if (keyValue2.isPresent) {
            log.info("keyValue2: {}", keyValue2.get())
        }
    }
}