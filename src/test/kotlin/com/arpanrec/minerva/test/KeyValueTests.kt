package com.arpanrec.minerva.test

import com.arpanrec.minerva.physical.KeyValue
import com.arpanrec.minerva.physical.KeyValuePersistence
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class KeyValueTests {

    private val keyValuePersistence = KeyValuePersistence()
    @Test
    fun testSave() {
        val keyValue = KeyValue("key", "value")
        keyValuePersistence.save(keyValue)
    }
}