package com.arpanrec.minerva.tfstate

import com.arpanrec.minerva.physical.KeyValue
import com.arpanrec.minerva.physical.KeyValuePersistence
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class StateManage(
    @Autowired private val keyValuePersistence: KeyValuePersistence
) {
    private val tfStateKeyPath = keyValuePersistence.internalStorageKey + "/tfstate"

    private val objectMapper = ObjectMapper()
    private val valueMapType: MapType = TypeFactory.defaultInstance().constructMapType(
        HashMap::class.java, String::class.java, Object::class.java
    )

    fun get(tfState: String): HttpEntity<Any> {
        val keyValueMaybe: Optional<KeyValue> = keyValuePersistence.get(key = "$tfStateKeyPath/$tfState")
        if (keyValueMaybe.isPresent) {
            val keyValue: KeyValue = keyValueMaybe.get()
            val result: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValue.value, valueMapType
            )
            return ResponseEntity(result, HttpStatus.OK)
        } else {
            return ResponseEntity("", HttpStatus.OK)
        }
    }

    fun createOrUpdate(
        tfState: String, tfStateJson: Map<String, Any>, tfStateLockID: String?
    ): HttpEntity<Map<String, Any>> {
        val optionalExistingStateData = keyValuePersistence.get(key = "$tfStateKeyPath/$tfState")
        val keyValueStateData = KeyValue(
            key = "$tfStateKeyPath/$tfState", value = objectMapper.writeValueAsString(tfStateJson)
        )
        if (tfStateLockID == null) {

            if (optionalExistingStateData.isPresent) {
                keyValuePersistence.update(keyValueStateData)
            } else {
                keyValuePersistence.save(keyValueStateData)
            }
            return ResponseEntity(tfStateJson, HttpStatus.OK)
        }
        val keyValueLockDataMaybe: Optional<KeyValue> = keyValuePersistence.get(key = "$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val lockDataMap: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            val existingLockID: String = lockDataMap["ID"] as String
            if (existingLockID == tfStateLockID) {
                if (optionalExistingStateData.isPresent) {
                    keyValuePersistence.update(keyValueStateData)
                } else {
                    keyValuePersistence.save(keyValueStateData)
                }
                return ResponseEntity(tfStateJson, HttpStatus.OK)
            } else {
                return ResponseEntity(lockDataMap, HttpStatus.CONFLICT)
            }
        } else {
            return ResponseEntity(
                mapOf(
                    "error" to "lock not found"
                ), HttpStatus.CONFLICT
            )
        }
    }

    fun setLock(tfState: String, tfStateLock: Map<String, Any>): HttpEntity<Map<String, Any>> {
        val keyValueLockDataMaybe: Optional<KeyValue> = keyValuePersistence.get(key = "$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val tfLockJson: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            return ResponseEntity(tfLockJson, HttpStatus.CONFLICT)
        } else {
            val keyValue = KeyValue(
                key = "$tfStateKeyPath/${tfState}.lock", value = objectMapper.writeValueAsString(tfStateLock)
            )
            keyValuePersistence.save(keyValue)
            return ResponseEntity(tfStateLock, HttpStatus.OK)
        }
    }

    fun deleteLock(tfState: String): HttpEntity<Any> {
        val keyValueLockDataMaybe: Optional<KeyValue> = keyValuePersistence.get(key = "$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            keyValuePersistence.deleteAllVersions(keyValueLockDataMaybe.get())
            return ResponseEntity("", HttpStatus.OK)
        } else {
            return ResponseEntity("", HttpStatus.OK)
        }
    }
}
