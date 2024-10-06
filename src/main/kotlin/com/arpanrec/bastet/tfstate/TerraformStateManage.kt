package com.arpanrec.bastet.tfstate

import com.arpanrec.bastet.physical.KVData
import com.arpanrec.bastet.physical.KVDataService
import com.arpanrec.bastet.physical.NameSpace
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
class TerraformStateManage(@Autowired private val kVDataService: KVDataService) {
    private val tfStateKeyPath = NameSpace.INTERNAL_TF_STATE

    private val objectMapper = ObjectMapper()
    private val valueMapType: MapType = TypeFactory.defaultInstance().constructMapType(
        HashMap::class.java, String::class.java, Object::class.java
    )

    fun get(tfState: String): HttpEntity<Any> {
        val keyValueMaybe: Optional<KVData> = kVDataService.getMaybe("$tfStateKeyPath/$tfState")
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
        val keyValueStateData = KVData(
            "$tfStateKeyPath/$tfState",
            objectMapper.writeValueAsString(tfStateJson),
            mapOf("created" to System.currentTimeMillis().toString())
        )
        if (tfStateLockID == null) {

            kVDataService.saveOrUpdate(keyValueStateData)
            return ResponseEntity(HttpStatus.OK)
        }
        val keyValueLockDataMaybe: Optional<KVData> = kVDataService.getMaybe("$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val lockDataMap: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            val existingLockID: String = lockDataMap["ID"] as String
            if (existingLockID == tfStateLockID) {
                kVDataService.saveOrUpdate(keyValueStateData)
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
        val keyValueLockDataMaybe: Optional<KVData> = kVDataService.getMaybe("$tfStateKeyPath/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            val tfLockJson: Map<String, Any> = objectMapper.readValue<Map<String, String>>(
                keyValueLockDataMaybe.get().value, valueMapType
            )
            return ResponseEntity(tfLockJson, HttpStatus.CONFLICT)
        } else {
            val keyValue = KVData(
                "$tfStateKeyPath/${tfState}.lock",
                objectMapper.writeValueAsString(tfStateLock), mapOf("created" to System.currentTimeMillis().toString())
            )
            kVDataService.saveOrUpdate(keyValue)
            return ResponseEntity(HttpStatus.OK)
        }
    }

    fun deleteLock(tfState: String): HttpEntity<Any> {
        val keyValueLockDataMaybe: Optional<KVData> = kVDataService.getMaybe("${tfStateKeyPath}/${tfState}.lock")
        if (keyValueLockDataMaybe.isPresent) {
            kVDataService.delete(keyValueLockDataMaybe.get())
            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.OK)
        }
    }
}
