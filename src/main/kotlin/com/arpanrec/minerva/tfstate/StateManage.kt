package com.arpanrec.minerva.tfstate

import com.arpanrec.minerva.physical.KVData
import com.arpanrec.minerva.physical.KeyValuePersistence
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.Optional

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
        val keyValueMaybe: Optional<KVData> = keyValuePersistence.get("$tfStateKeyPath/$tfState")
        if (keyValueMaybe.isPresent) {
            val keyValue: KVData = keyValueMaybe.get()
            val result: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValue.value, valueMapType
            )
            return ResponseEntity(result, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.OK)
        }
    }

    fun createOrUpdate(
        tfState: String, tfStateJson: Map<String, Any>, tfStateLockID: String?
    ): HttpEntity<Map<String, Any>> {
        val optionalExistingStateData = keyValuePersistence.get(key = "$tfStateKeyPath/$tfState")
        val keyValueStateData = KVData(
            objectMapper.writeValueAsString(tfStateJson), mapOf("created" to System.currentTimeMillis().toString())
        )
        if (tfStateLockID == null) {

            if (optionalExistingStateData.isPresent) {
                keyValuePersistence.update("$tfStateKeyPath/$tfState", keyValueStateData)
            } else {
                keyValuePersistence.save("$tfStateKeyPath/$tfState", keyValueStateData)
            }
            return ResponseEntity(HttpStatus.OK)
        }
        val keyValueLockDataMaybe: Optional<KVData> = keyValuePersistence.get("$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val lockDataMap: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            val existingLockID: String = lockDataMap["ID"] as String
            if (existingLockID == tfStateLockID) {
                if (optionalExistingStateData.isPresent) {
                    keyValuePersistence.update("$tfStateKeyPath/$tfState", keyValueStateData)
                } else {
                    keyValuePersistence.save("$tfStateKeyPath/$tfState", keyValueStateData)
                }
                return ResponseEntity(HttpStatus.OK)
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
        val keyValueLockDataMaybe: Optional<KVData> = keyValuePersistence.get("$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val tfLockJson: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            return ResponseEntity(tfLockJson, HttpStatus.CONFLICT)
        } else {
            val keyValue = KVData(
                objectMapper.writeValueAsString(tfStateLock), mapOf("created" to System.currentTimeMillis().toString())
            )
            keyValuePersistence.save("$tfStateKeyPath/${tfState}.lock", keyValue)
            return ResponseEntity(HttpStatus.OK)
        }
    }

    fun deleteLock(tfState: String): HttpEntity<Any> {
        val keyValueLockDataMaybe: Optional<KVData> = keyValuePersistence.get("${tfStateKeyPath}/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            keyValuePersistence.delete("${tfStateKeyPath}/${tfState}.lock")
            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.OK)
        }
    }
}
