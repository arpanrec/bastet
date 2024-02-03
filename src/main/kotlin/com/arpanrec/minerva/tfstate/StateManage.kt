package com.arpanrec.minerva.tfstate

import com.arpanrec.minerva.physical.KeyValue
import com.arpanrec.minerva.physical.KeyValuePersistence
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.Optional
import kotlin.collections.HashMap


@Component
class StateManage(
    @Autowired private val keyValuePersistence: KeyValuePersistence
) {
    private val tfStateKeyPath = keyValuePersistence.internalStorageKey + "/tfstate"

    fun get(tfState: String): HttpEntity<Any> {
        val keyValueMaybe: Optional<KeyValue> = keyValuePersistence.get(key = "$tfStateKeyPath/$tfState")
        return if (keyValueMaybe.isPresent) {
            val keyValue: KeyValue = keyValueMaybe.get()
            val result: Map<String, Any> = ObjectMapper().readValue<Map<String, String>>(
                keyValue.value, TypeFactory.defaultInstance().constructMapType(
                    HashMap::class.java,
                    String::class.java,
                    Object::class.java
                )
            )
            ResponseEntity(result, null, HttpStatus.OK)
        } else {
            ResponseEntity("", null, HttpStatus.OK)
        }
    }

    fun createOrUpdate(
        tfState: String,
        tfStateJson: Map<String, Any>,
        tfStateLockID: String?
    ): HttpEntity<Map<String, Any>> {
        val keyValue =
            KeyValue(key = "$tfStateKeyPath/$tfState", value = ObjectMapper().writeValueAsString(tfStateJson))
        keyValuePersistence.save(keyValue)
        return ResponseEntity(tfStateJson, null, HttpStatus.OK)
    }
}